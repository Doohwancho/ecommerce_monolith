package com.cho.ecommerce.domain.member.service;

import com.cho.ecommerce.api.domain.RegisterPostDTO;
import com.cho.ecommerce.domain.member.entity.AuthorityEntity;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.entity.UserAuthorityEntity;
import com.cho.ecommerce.domain.member.repository.AuthorityRepository;
import com.cho.ecommerce.domain.member.repository.UserAuthorityRepository;
import com.cho.ecommerce.domain.member.repository.UserRepository;
import com.cho.ecommerce.domain.member.mapper.UserMapper;
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
        UserEntity savedUserEntity = userRepository.save(userEntity);
        
        return savedUserEntity;
    }
    
    @Transactional
    public UserEntity saveRoleUser(RegisterPostDTO registerPostDTO) {
        UserEntity userEntity = userMapper.dtoToEntityWithNestedAddress(registerPostDTO, "USER");
        return saveRoleUser(userEntity);
    }
    
    @Transactional
    public UserEntity saveRoleAdmin(RegisterPostDTO registerPostDTO) {
        UserEntity userEntity = userMapper.dtoToEntityWithNestedAddress(registerPostDTO, "ADMIN");
        return saveRoleUser(userEntity);
    }
    
    public Optional<UserEntity> findUser(String userId) {
        return userRepository.findByUserId(userId); //TODO 5 - Optional을 반환타입으로 하면 안좋다고 effective java에서 말한거 같은데?
    }
    
    @Transactional
    public boolean updateUserName(String userId, String userName) {
        Optional<UserEntity> userOptional = userRepository.findByUserId(userId);
        userOptional.ifPresent(user -> {
            user.setName(userName);
            user.setUpdated(LocalDateTime.now());
            userRepository.save(user);
        });
        return userOptional.isPresent();
    }
    
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        return userRepository.findByUserId(userId)
            .orElseThrow(() -> new UsernameNotFoundException(userId + "이 존재하지 않음"));
    }
    
    @Transactional
    public boolean addAuthorityROLE_USER(String userId, String authority) {
        Optional<UserEntity> userOptional = userRepository.findByUserId(userId);
        userOptional.ifPresent(user -> {
            // Create and save the user's authority
            AuthorityEntity userRole = authorityRepository.findByAuthority(AuthorityEntity.ROLE_USER)
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
    public boolean removeAuthority(String userId, String authority) {
        Optional<UserEntity> userOptional = userRepository.findByUserId(userId);
        userOptional.ifPresent(user -> {
            user.getUserAuthorities().remove(new AuthorityEntity(authority)); //TODO - 검증: 정말 authority가 지워지는지 확인하기
            user.setUpdated(LocalDateTime.now());
            userRepository.save(user);
        });
        return userOptional.isPresent();
    }
    
//    public void clearUsers() {
//        userRepository.deleteAll();
//    }
    
//    public Page<User> listUsers(Integer page, Integer size) {
//        return userRepository.findAll(PageRequest.of(page - 1, size));
//    }
    
//    public Map<String, User> getUserMap(Collection<String> userIds) {
//        if (userIds == null || userIds.isEmpty()) {
//            return new HashMap<>();
//        }
//        return StreamSupport.stream(userRepository.findAllById(userIds).spliterator(), false)
//            .collect(Collectors.toMap(User::getUsername, Function.identity()));
//    }
}