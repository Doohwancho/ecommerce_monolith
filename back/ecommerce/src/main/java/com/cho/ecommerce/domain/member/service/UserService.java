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
import com.cho.ecommerce.global.error.exception.business.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    
    private final AuthorityRepository authorityRepository;
    private final UserAuthorityRepository userAuthorityRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    
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
        return saveRoleUser(userEntity);
    }
    
    @Transactional
    public UserEntity saveRoleAdmin(RegisterPostDTO registerPostDTO) {
        UserEntity userEntity = userMapper.dtoToEntityWithNestedAddress(registerPostDTO,
            "ROLE_ADMIN");
        return saveRoleUser(userEntity);
    }
    
    public User findUserByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(
            () -> new ResourceNotFoundException("User not found, userId: "
                + username));//TODO 5 - Optional을 반환타입으로 하면 안좋다고 effective java에서 말한거 같은데?
        
        return userMapper.toUser(userEntity);
    }
    
    public UserDetailsDTO findUserDetailsDTOByUsername(String username) {
        User user = findUserByUsername(username);
        return userMapper.toUserDetailsDTO(user);
    }
    
    
    @Transactional
    public boolean updateUserName(String username, String userName) {
        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        userOptional.ifPresent(user -> {
            user.setName(userName);
            user.setUpdated(LocalDateTime.now());
            userRepository.save(user);
        });
        return userOptional.isPresent();
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(username + "이 존재하지 않음"));
    }
    
    @Transactional
    public boolean addAuthorityRoleUser(String username, String authority) {
        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
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
        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        userOptional.ifPresent(user -> {
            user.getUserAuthorities()
                .remove(new AuthorityEntity(authority)); //TODO - 검증: 정말 authority가 지워지는지 확인하기
            user.setUpdated(LocalDateTime.now());
            userRepository.save(user);
        });
        return userOptional.isPresent();
    }
    
}