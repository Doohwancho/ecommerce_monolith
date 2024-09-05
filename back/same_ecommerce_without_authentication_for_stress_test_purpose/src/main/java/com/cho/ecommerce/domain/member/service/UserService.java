package com.cho.ecommerce.domain.member.service;

import com.cho.ecommerce.domain.member.domain.User;
import com.cho.ecommerce.domain.member.entity.AuthorityEntity;
import com.cho.ecommerce.domain.member.entity.UserAuthorityEntity;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.mapper.UserMapper;
import com.cho.ecommerce.domain.member.repository.AuthorityRepository;
import com.cho.ecommerce.domain.member.repository.UserAuthorityRepository;
import com.cho.ecommerce.domain.member.repository.UserRepository;
import com.cho.ecommerce.global.error.ErrorCode;
import com.cho.ecommerce.global.error.exception.business.ResourceNotFoundException;
import com.cho.ecommerce.global.error.exception.member.InvalidatingSessionForUser;
import com.cho.ecommerce.global.error.exception.member.LockedAccountUserFailedToAuthenticate;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


//@AllArgsConstructor
@Service
public class UserService {
    
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final AuthorityRepository authorityRepository;
    private final UserAuthorityRepository userAuthorityRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
//    private final SessionRegistry sessionRegistry; //@Lazy in order to avoid circular reference error with SecurityConfig.java
    
    @Autowired
    public UserService(AuthorityRepository authorityRepository,
        UserAuthorityRepository userAuthorityRepository,
        UserRepository userRepository,
        UserMapper userMapper
//        @Lazy SessionRegistry sessionRegistry
    ) {
        this.authorityRepository = authorityRepository;
        this.userAuthorityRepository = userAuthorityRepository;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
//        this.sessionRegistry = sessionRegistry;
    }
    
    
    @Transactional
    public UserEntity saveRoleUser(UserEntity userEntity) {
        //1. get user's authority
        AuthorityEntity userRole = authorityRepository.findByAuthority(AuthorityEntity.ROLE_USER)
            .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
        
        //2. save into user-authority table
        UserAuthorityEntity userAuthorityEntity = new UserAuthorityEntity();
        userAuthorityEntity.setUserEntity(userEntity);
        userAuthorityEntity.setAuthorityEntity(userRole);
        
        //3. save user
        userEntity.setUserAuthorities(userAuthorityEntity);
        return userRepository.save(userEntity);
    }
    
    public UserEntity findUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found, userId: " + id));
    }
    
    public User findUserByUsername(String username) {
        UserEntity userEntity = userRepository.findUserDetailsByUsername(username).orElseThrow(
            () -> new ResourceNotFoundException("User not found, userId: "
                + username));
        
        return userMapper.toUser(userEntity);
    }
    
    @Transactional
    public boolean updateUserName(String username, String userName) {
        Optional<UserEntity> userOptional = userRepository.findUserDetailsByUsername(username);
        userOptional.ifPresent(user -> {
            user.setName(userName);
            user.setUpdated(LocalDateTime.now());
            userRepository.save(user);
        });
        return userOptional.isPresent();
    }
    
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        UserEntity userEntity = userRepository.findUserDetailsByUsername(username)
//            .orElseThrow(() -> new UsernameNotFoundException(username + "이 존재하지 않음"));
//
//        if (!(userEntity.isEnabled() && userEntity.isAccountNonExpired()
//            && userEntity.isAccountNonLocked() && userEntity.isCredentialsNonExpired())) {
//            log.warn("locked account user authentication failed! username: " + username);
//            throw new LockedAccountUserFailedToAuthenticate(
//                ErrorCode.LOCKED_USER_FAILED_TO_AUTHENTICATE);
//        }
//
//        return userEntity;
//    }
    
    @Transactional
    public boolean addAuthorityRoleUser(String username, String authority) {
        Optional<UserEntity> userOptional = userRepository.findUserDetailsByUsername(username);
        userOptional.ifPresent(user -> {
            // Create and save the user's authority
            AuthorityEntity userRole = authorityRepository.findByAuthority(
                    AuthorityEntity.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
            
            UserAuthorityEntity userAuthorityEntity = new UserAuthorityEntity();
            userAuthorityEntity.setUserEntity(user);
            userAuthorityEntity.setAuthorityEntity(userRole);
            
            userAuthorityRepository.save(
                userAuthorityEntity); //TODO - 이 부분 빼도 cascade 되서 저장되지 않을까? 그러면 @Transactional 빼도 되지 않을까?
            
            user.setUserAuthorities(userAuthorityEntity);
            user.setUpdated(LocalDateTime.now());
            userRepository.save(user);
        });
        return userOptional.isPresent();
    }
    
    @Transactional
    public boolean removeAuthority(String username, String authority) {
        Optional<UserEntity> userOptional = userRepository.findUserDetailsByUsername(username);
        userOptional.ifPresent(user -> {
            user.getUserAuthorities()
                .remove(new AuthorityEntity(authority)); //TODO - 검증: 정말 authority가 지워지는지 확인하기
            user.setUpdated(LocalDateTime.now());
            userRepository.save(user);
        });
        return userOptional.isPresent();
    }
    
    
    public void incrementFailedAttempts(UserEntity user) {
//        user.setFailedAttempt(user.getFailedAttempt() + 1);
//        if (user.getFailedAttempt() >= SecurityConstants.MAX_LOGIN_ATTEMPTS) {
//            user.setEnabled(false); //lock user account
//            invalidateUserSessions(user.getUsername()); // Invalidate session
//            log.warn(user.getUsername()
//                + "has failed to login more than 5 times, therefore account has become locked.");
//        }
//        userRepository.save(user);
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
    
    private void invalidateUserSessions(String username) {
    
//        try {
//            for (SessionInformation session : sessionRegistry.getAllSessions(username, false)) {
//                session.expireNow();
//            }
//        } catch (Exception e) {
//            log.error("Error invalidating sessions for user: " + username, e);
//            throw new InvalidatingSessionForUser(ErrorCode.INVALIDATING_SESSION_FOR_USER);
//        }
    }
    
    public void invalidateUserSessionAndLockUser(UserEntity user) {
        user.setEnabled(false); //lock user account
        invalidateUserSessions(user.getUsername()); // Invalidate session
        
        userRepository.save(user);
    }
}