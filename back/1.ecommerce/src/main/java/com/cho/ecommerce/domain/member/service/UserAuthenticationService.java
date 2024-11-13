package com.cho.ecommerce.domain.member.service;

import com.cho.ecommerce.domain.common.email.adapter.EmailAdapter;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.repository.UserRepository;
import com.cho.ecommerce.global.config.security.SecurityConstants;
import com.cho.ecommerce.global.error.ErrorCode;
import com.cho.ecommerce.global.error.exception.business.ResourceNotFoundException;
import com.cho.ecommerce.global.error.exception.member.InvalidatingSessionForUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthenticationService {
    
    private final EmailAdapter emailAdapter;
    private final UserRepository userRepository;
    private final SessionRegistry sessionRegistry;
    
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
        emailAdapter.sendAccountLockedNotification(user.getEmail());
    }
    
    private void invalidateUserSessions(String username) {
        try {
            for (SessionInformation session : sessionRegistry.getAllSessions(username, false)) {
                session.expireNow();
            }
            //Q. 다른 사용자 인증정보도 같이 삭제하는거 아냐?
            //A. 아님. SecurityContextHolder.clearContext()는 현재 스레드의 SecurityContext만 삭제합니다.
            //   Spring Security는 사용자별로 ThreadLocal을 사용하기 때문에, 다른 사용자의 인증 정보는 삭제되지 않습니다
            //   현재 요청 스레드에서만 영향을 미치므로, 다른 사용자의 세션은 그대로 유지됩니다.
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            log.error("Error invalidating sessions for user: " + username, e);
            throw new InvalidatingSessionForUser(ErrorCode.INVALIDATING_SESSION_FOR_USER);
        }
    }
    
    
    public void resetFailedAttempts(String username) {
        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("user not found"));
        if (user == null) {
            log.warn("user not found! therefore, resetFailedAttempts() failed!" + username);
            throw new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        user.setFailedAttempt(0);
        userRepository.save(user);
    }
}
