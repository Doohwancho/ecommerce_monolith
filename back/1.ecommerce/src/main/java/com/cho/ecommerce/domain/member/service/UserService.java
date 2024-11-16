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
import com.cho.ecommerce.global.error.exception.member.DuplicateUsernameException;
import com.cho.ecommerce.global.error.exception.member.LockedAccountUserFailedToAuthenticate;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
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
        // Check if username already exists
        if (userRepository.existsByUsername(userEntity.getUsername())) {
            throw new DuplicateUsernameException(userEntity.getUsername());
        }
        
        //1. get user's authority
        AuthorityEntity userRole = authorityRepository.findByAuthority(AuthorityEntity.ROLE_USER)
            .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
        
        //2. save into user-authority table
        UserAuthorityEntity userAuthorityEntity = new UserAuthorityEntity();
        userAuthorityEntity.setUserEntity(userEntity);
        userAuthorityEntity.setAuthorityEntity(userRole);
        
        //3. save user
        userEntity.setUserAuthorities(userAuthorityEntity);
        try {
            return userRepository.save(userEntity);
        } catch (DataIntegrityViolationException e) {
            // Handle concurrent registration attempts
            if (e.getMessage().contains("uk_user_id")) {
                throw new DuplicateUsernameException(userEntity.getUsername());
            }
            throw e;
        }
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
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findUserDetailsByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(username + "이 존재하지 않음"));
        
        if (!(userEntity.isEnabled() && userEntity.isAccountNonExpired()
            && userEntity.isAccountNonLocked() && userEntity.isCredentialsNonExpired())) {
            log.warn("locked account user authentication failed! username: " + username);
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
}