package com.cho.ecommerce.domain.member.service;

import com.cho.ecommerce.domain.member.domain.Email;
import com.cho.ecommerce.domain.member.domain.VerificationCode;
import com.cho.ecommerce.domain.member.entity.AddressEntity;
import com.cho.ecommerce.domain.member.entity.AuthorityEntity;
import com.cho.ecommerce.domain.member.entity.InactiveMemberEntity;
import com.cho.ecommerce.domain.member.entity.UserAuthorityEntity;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.repository.AuthorityRepository;
import com.cho.ecommerce.domain.member.repository.InactiveMemberRepository;
import com.cho.ecommerce.domain.member.repository.UserRepository;
import com.cho.ecommerce.global.config.security.SecurityConstants;
import com.cho.ecommerce.global.error.ErrorCode;
import com.cho.ecommerce.global.error.exception.business.ResourceNotFoundException;
import com.cho.ecommerce.global.error.exception.member.InvalidPasswordException;
import com.cho.ecommerce.global.error.exception.member.InvalidatingSessionForUser;
import com.cho.ecommerce.global.error.exception.member.MaxAttemptsExceededException;
import com.cho.ecommerce.global.error.exception.member.TooManyRequestsException;
import com.cho.ecommerce.global.error.exception.member.UnauthorizedAccessException;
import com.cho.ecommerce.global.error.exception.member.VerificationCodeAlreadyExistsException;
import com.cho.ecommerce.global.error.exception.member.VerificationException;
import com.google.common.util.concurrent.RateLimiter;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserVerificationService {
    
    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private InactiveMemberRepository inactiveMemberRepository;
    private final SessionRegistry sessionRegistry;
    private final JavaMailSender emailSender;
    private final ConcurrentHashMap<String, VerificationCode> verificationCodes = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Email> userEmails = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LocalDateTime> verifiedUsers = new ConcurrentHashMap<>();
    
    private final SecureRandom secureRandom = new SecureRandom();
    private final RateLimiter rateLimiter = RateLimiter.create(1.0); // 1 request per second
    private final PasswordEncoder passwordEncoder;
    
    private static final int VERIFICATION_CODE_LENGTH = 6;
    private static final int USER_EMAIL_EXPIRY_MINUTES = 10;
    private static final int VERIFICATION_CODE_EXPIRY_MINUTES = 5;
    private static final int VERIFICATION_STATE_EXPIRY_MINUTES = 5;
    private static final int MAX_VERIFICATION_ATTEMPTS = 3;
    private static final String CODE_CHARS = "0123456789";
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    
    public void incrementFailedAttempts(UserEntity user) {
        user.setFailedAttempt(user.getFailedAttempt() + 1);
        if (user.getFailedAttempt() >= SecurityConstants.MAX_LOGIN_ATTEMPTS) {
            user.setEnabled(false); //lock user account
            invalidateUserSessions(user.getUsername()); // Invalidate session
            log.warn(user.getUsername()
                + "has failed to login more than 5 times, therefore account has become locked.");
        }
        userRepository.save(user);
    }
    
    public void invalidateUserSessionAndLockUser(UserEntity user) {
        user.setEnabled(false); //lock user account
        invalidateUserSessions(user.getUsername()); // Invalidate session
        
        userRepository.save(user);
        sendEmailYourAccountHasBeenLockedMsg(user.getEmail());
    }
    
    private void invalidateUserSessions(String username) {
        try {
            for (SessionInformation session : sessionRegistry.getAllSessions(username, false)) {
                session.expireNow();
            }
        } catch (Exception e) {
            log.error("Error invalidating sessions for user: " + username, e);
            throw new InvalidatingSessionForUser(ErrorCode.INVALIDATING_SESSION_FOR_USER);
        }
    }
    
    
    public void resetFailedAttempts(String username) {
        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            log.warn("user not found! therefore, resetFailedAttempts() failed!" + username);
            throw new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        user.setFailedAttempt(0);
        userRepository.save(user);
    }
    
    
    public Boolean findUserExistsByUserId(String userId) {
        return userRepository.existsByUserId(userId);
    }
    
    /*
        # Send verification code
        POST /api/verification/send
        {
            "userId": "thisIsUserId1234"
        }
     */
    public void sendVerificationCodeByUserId(String userId) {
        // 1) check if user exists in either member table or inactive member table and get email
        String toEmail = findUserEmailById(userId);
        
        //2) email validation
        if (!EmailValidator.getInstance().isValid(toEmail)) {
            log.error("Invalid email address: {}", toEmail);
            throw new IllegalArgumentException("Invalid email address");
        }
        
        //3) rate-limit
        if (!rateLimiter.tryAcquire()) {
            log.warn("Rate limit exceeded for email: {}", toEmail);
            throw new TooManyRequestsException("Please wait before requesting another code");
        }
        
        try {
            //4) generate 6 digit code
            String code = generateVerificationCode();
            LocalDateTime expiryTimeForCode = LocalDateTime.now()
                .plusMinutes(VERIFICATION_CODE_EXPIRY_MINUTES);
            
            //5) Store or update verification code
            verificationCodes.compute(toEmail, (key, existingCode) -> {
                if (existingCode != null && LocalDateTime.now()
                    .isBefore(existingCode.getExpiryTime())) {
                    log.warn("Existing valid code found for email: {}", toEmail);
                    throw new VerificationCodeAlreadyExistsException(
                        "Valid verification code already exists");
                }
                return new VerificationCode(code, expiryTimeForCode, 0);
            });
            
            //6) sending code to email
            sendEmailVerificationCode(toEmail, code);
            
            //7) save user's email (save db i/o)
            LocalDateTime expiryTimeForUserEmail = LocalDateTime.now()
                .plusMinutes(USER_EMAIL_EXPIRY_MINUTES);
            
            userEmails.put(userId,
                new Email(toEmail, expiryTimeForUserEmail));
            
            //logging
//            log.info("Verification code sent successfully to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send verification code to {}: {}", toEmail, e.getMessage());
            throw new VerificationException("Failed to process verification request");
        }
    }
    
    private String findUserEmailById(String userId) {
        // First try active users
        Optional<UserEntity> activeUser = userRepository.findByUserId(userId);
        if (activeUser.isPresent()) {
            return activeUser.get().getEmail();
        }
        
        // Then try inactive users
        Optional<InactiveMemberEntity> inactiveUser = inactiveMemberRepository.findByUsername(
            userId);
        if (inactiveUser.isPresent()) {
            return inactiveUser.get().getEmail();
        }
        
        throw new ResourceNotFoundException("User not found, userId: " + userId);
    }
    
    /*
        # Verify code
        POST /api/verification/verify
        {
            "userId": "thisIsUserId1234",
            "code": "123456"
        }
     */
    public boolean verifyCode(String userId, String code) {
        //1) get userEmail address by userId. if don't exists, throw Exception
        Email email = userEmails.get(userId);
        if (email == null) {
            log.warn("No user email found from previous verification step: {}",
                userId); //TODO - verifyCode() 내부에 에러나면 log.warn()으로 처리하는게 맞을까, 아니면 throw new Exception() 처리가 맞을까? 해커에게 필요 이상의 정보를 주는게 아닐까?
            return false;
        }
        
        //2) verify if stored code exists
        VerificationCode storedCode = verificationCodes.get(email.getValue());
        
        if (storedCode == null) {
            log.warn("No verification code found for email: {}", email);
            return false;
        }
        
        //3) Check expiry
        if (LocalDateTime.now().isAfter(storedCode.getExpiryTime())) {
            log.warn("Verification code expired for email: {}", email);
            verificationCodes.remove(email.getValue());
            return false;
        }
        
        //4) Check attempts
        if (storedCode.getAttempts() >= MAX_VERIFICATION_ATTEMPTS) {
            log.warn("Max verification attempts exceeded for email: {}", email);
            verificationCodes.remove(email.getValue());
            throw new MaxAttemptsExceededException("Maximum verification attempts exceeded");
        }
        
        //5) Update attempts
        storedCode.incrementAttempts();
        
        //6) Verify code
        boolean isValid = storedCode.getCode().equals(code);
        if (isValid) {
//            log.info("Code verified successfully for email: {}", email);
            verificationCodes.remove(email.getValue());
            
            //7) Re-activate user with temporary password
            reactivateUserAfterVerification(userId);
            
            verifiedUsers.put(userId,
                LocalDateTime.now().plusMinutes(VERIFICATION_STATE_EXPIRY_MINUTES));
        } else {
            log.warn("Invalid code attempt for email: {}", email);
        }
        
        return isValid;
    }
    
    private void sendEmailVerificationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Your Verification Code");
        message.setText(String.format(
            "Your verification code is: %s\n" +
                "This code will expire in %d minutes.\n" +
                "If you didn't request this code, please ignore this email.",
            code, VERIFICATION_CODE_EXPIRY_MINUTES));
        
        emailSender.send(message);
    }
    
    public void sendEmailYourAccountHasBeenLockedMsg(String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Your Accounts has been Locked");
        message.setText(
            "Abnormal Activity Detected. %s\n" +
                "Your Account has been locked.\n" +
                "To Reactivate account, click 'forgot password?' on login page." +
                "If you didn't request this code, please ignore this email.");
        
        emailSender.send(message);
    }
    
    private String generateVerificationCode() {
        StringBuilder code = new StringBuilder(VERIFICATION_CODE_LENGTH);
        for (int i = 0; i < VERIFICATION_CODE_LENGTH; i++) {
            code.append(CODE_CHARS.charAt(secureRandom.nextInt(CODE_CHARS.length())));
        }
        return code.toString();
    }
    
    @Transactional
    public void reactivateUserAfterVerification(String userId) {
        // First check if user is in active members table
        Optional<UserEntity> activeUser = userRepository.findByUserId(userId);
        if (activeUser.isPresent()) {
            UserEntity user = activeUser.get();
            user.setEnabled(true);
            user.setFailedAttempt(0);
            userRepository.save(user);
            return;
        }
        
        InactiveMemberEntity inactiveMember = inactiveMemberRepository.findByUsername(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Inactive user not found"));
        
        // 1. Create Address Entity
        AddressEntity address = AddressEntity.builder()
            .street(inactiveMember.getStreet())
            .city(inactiveMember.getCity())
            .state(inactiveMember.getState())
            .country(inactiveMember.getCountry())
            .zipCode(inactiveMember.getZipCode())
            .build();
        
        // 2. Create User Entity
        UserEntity user = UserEntity.builder()
            .username(inactiveMember.getUsername())
            .email(inactiveMember.getEmail())
            .name(inactiveMember.getName())
            .password(inactiveMember.getPassword())
            .role(inactiveMember.getRole())
            .failedAttempt(0)
            .enabled(true)
            .created(inactiveMember.getCreatedAt())
            .updated(LocalDateTime.now())
            .address(address)
            .build();
        
        // 3. Set bidirectional relationship
        address.setUser(user);
        
        // 4. Create User Authority
        AuthorityEntity authority = authorityRepository.findByAuthority(inactiveMember.getRole())
            .orElseGet(() -> authorityRepository.save(AuthorityEntity.builder()
                .authority(inactiveMember.getRole())
                .build()));
        
        UserAuthorityEntity userAuthority = UserAuthorityEntity.builder()
            .userEntity(user)
            .authorityEntity(authority)
            .build();
        
        // 5. Set User Authorities
        Set<UserAuthorityEntity> authorities = new HashSet<>();
        authorities.add(userAuthority);
        user.setUserAuthorities(authorities);
        
        // 6. Save User (will cascade save address and authorities)
        userRepository.save(user);
        
        // 7. Delete from inactive members
        inactiveMemberRepository.delete(inactiveMember);
    }
    
    @Transactional
    public void setPasswordAfterVerification(String userId, String newPassword) {
        // Check if user was verified and verification hasn't expired
        LocalDateTime expiryTime = verifiedUsers.get(userId);
        if (expiryTime == null || LocalDateTime.now().isAfter(expiryTime)) {
            verifiedUsers.remove(userId); // Cleanup expired entry if exists
            throw new UnauthorizedAccessException("User not verified or verification expired");
        }
        
        UserEntity user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Validate password history (optional)
//        if (passwordEncoder.matches(newPassword, user.getPassword())) {
        if (newPassword.equals(user.getPassword())) {
            throw new InvalidPasswordException("New password must be different from old password");
        }
        
        // Update password
//        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPassword(newPassword);
//        user.setPasswordUpdatedAt(LocalDateTime.now()); //TODO - tracking last password saved date, and if over 3, 6 months, notive user to re-make password
        userRepository.save(user);
        
        // Remove from verified users after password reset
        verifiedUsers.remove(userId);
    }
    
    @Scheduled(fixedRate = 1800000) // Every 30m, remove expired code
    public void cleanupExpiredCodes() {
        verificationCodes.entrySet().removeIf(entry -> {
            boolean isExpired = LocalDateTime.now().isAfter(entry.getValue().getExpiryTime());
            if (isExpired) {
                log.debug("Removing expired code for email: {}", entry.getKey());
            }
            return isExpired;
        });
    }
    
    @Scheduled(fixedRate = 600000) // Every 10m, remove saved user-email for verification
    public void cleanupExpiredEmails() {
        userEmails.entrySet().removeIf(entry -> {
            boolean isExpired = LocalDateTime.now().isAfter(entry.getValue().getExpiryTime());
            if (isExpired) {
                log.debug("Removing user emails saved for verification. userId: {}",
                    entry.getKey());
            }
            return isExpired;
        });
    }
    
    @Scheduled(fixedRate = 600000) // Every 10 minutes, removed expired verification token
    public void cleanupExpiredVerifications() {
        LocalDateTime now = LocalDateTime.now();
        verifiedUsers.entrySet().removeIf(entry -> now.isAfter(entry.getValue()));
    }
}