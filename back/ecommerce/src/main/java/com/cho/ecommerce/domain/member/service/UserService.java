package com.cho.ecommerce.domain.member.service;

import com.cho.ecommerce.api.domain.RegisterPostDTO;
import com.cho.ecommerce.api.domain.UserDetailsDTO;
import com.cho.ecommerce.domain.member.domain.User;
import com.cho.ecommerce.domain.member.entity.AuthorityEntity;
import com.cho.ecommerce.domain.member.entity.UserAuthorityEntity;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.mapper.UserMapper;
import com.cho.ecommerce.domain.member.repository.AuthorityRepository;
import com.cho.ecommerce.domain.member.repository.UserAuthorityRepository;
import com.cho.ecommerce.domain.member.repository.UserRepository;
import com.cho.ecommerce.global.config.security.SecurityConstants;
import com.cho.ecommerce.global.error.ErrorCode;
import com.cho.ecommerce.global.error.exception.business.ResourceNotFoundException;
import com.cho.ecommerce.global.error.exception.member.InvalidatingSessionForUser;
import com.cho.ecommerce.global.error.exception.member.LockedAccountUserFailedToAuthenticate;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


//@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    
    private final AuthorityRepository authorityRepository;
    private final UserAuthorityRepository userAuthorityRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SessionRegistry sessionRegistry; //@Lazy in order to avoid circular reference error with SecurityConfig.java
    
    @Autowired
    public UserService(AuthorityRepository authorityRepository,
        UserAuthorityRepository userAuthorityRepository,
        UserRepository userRepository,
        UserMapper userMapper,
        @Lazy SessionRegistry sessionRegistry) {
        this.authorityRepository = authorityRepository;
        this.userAuthorityRepository = userAuthorityRepository;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.sessionRegistry = sessionRegistry;
    }
    
    
    @Transactional
    public UserEntity saveRoleUser(UserEntity userEntity) {
        //2. Create and save the user's authority
        AuthorityEntity userRole = authorityRepository.findByAuthority(AuthorityEntity.ROLE_USER)
            .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
        
        UserAuthorityEntity userAuthorityEntity = new UserAuthorityEntity();
        userAuthorityEntity.setUserEntity(userEntity);
        userAuthorityEntity.setAuthorityEntity(userRole);
        
        userAuthorityRepository.save(userAuthorityEntity);
        
        //3. save user
        userEntity.setUserAuthorities(userAuthorityEntity);
        return userRepository.save(userEntity);
    }
    
    @Transactional
    public UserEntity saveRoleUser(RegisterPostDTO registerPostDTO) {
        UserEntity userEntity = userMapper.dtoToEntityWithNestedAddress(registerPostDTO,
            "ROLE_USER");
        userEntity.setEnabled(true);
        userEntity.setFailedAttempt(0);
        return saveRoleUser(userEntity);
    }
    
    @Transactional
    public UserEntity saveRoleAdmin(RegisterPostDTO registerPostDTO) {
        UserEntity userEntity = userMapper.dtoToEntityWithNestedAddress(registerPostDTO,
            "ROLE_ADMIN");
        userEntity.setEnabled(true);
        userEntity.setFailedAttempt(0);
        return saveRoleUser(userEntity);
    }
    
    public User findUserByUsername(String username) {
        UserEntity userEntity = userRepository.findUserDetailsByUsername(username).orElseThrow(
            () -> new ResourceNotFoundException("User not found, userId: "
                + username));
        
        return userMapper.toUser(userEntity);
    }
    
    public UserDetailsDTO findUserDetailsDTOByUsername(String username) {
        User user = findUserByUsername(username);
        return userMapper.toUserDetailsDTO(user);
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
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findUserDetailsByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(username + "이 존재하지 않음"));
        
        if (!(userEntity.isEnabled() && userEntity.isAccountNonExpired()
            && userEntity.isAccountNonLocked() && userEntity.isCredentialsNonExpired())) {
            log.error("locked account user authentication failed! username: " + username);
            throw new LockedAccountUserFailedToAuthenticate(
                ErrorCode.LOCKED_USER_FAILED_TO_AUTHENTICATE);
        }
        
        return userEntity;
    }
    
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
            
            userAuthorityRepository.save(userAuthorityEntity);
            
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
        user.setFailedAttempt(user.getFailedAttempt() + 1);
        if (user.getFailedAttempt() >= SecurityConstants.MAX_LOGIN_ATTEMPTS) {
            user.setEnabled(false); //lock user account
            invalidateUserSessions(user.getUsername()); // Invalidate session
        }
        userRepository.save(user);
    }

    public void resetFailedAttempts(String username) {
        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            log.error("resetFailedAttempts() failed! username: " + username);
            throw new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        user.setFailedAttempt(0);
        userRepository.save(user);
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
}