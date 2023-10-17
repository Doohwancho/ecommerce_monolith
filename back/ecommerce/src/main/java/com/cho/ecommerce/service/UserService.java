package com.cho.ecommerce.service;

import com.cho.ecommerce.domain.Authority;
import com.cho.ecommerce.domain.User;
import com.cho.ecommerce.domain.UserAuthority;
import com.cho.ecommerce.repository.AuthorityRepository;
import com.cho.ecommerce.repository.UserAuthorityRepository;
import com.cho.ecommerce.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    
    private final AuthorityRepository authorityRepository;
    private final UserAuthorityRepository userAuthorityRepository;
    private final UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Transactional
    public User save(User user) {
        //1. set data into user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreated(LocalDateTime.now());
        user.setUpdated(LocalDateTime.now());
        user.setRole("USER");
        user.setEnabled(true);
        
        //2. Save the user first
        User savedUser = userRepository.save(user);
    
        //3. Create and save the user's authority
        Authority userRole = authorityRepository.findByAuthority(Authority.ROLE_USER)
            .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
        
        UserAuthority userAuthority = new UserAuthority();
        userAuthority.setUser(savedUser);
        userAuthority.setAuthority(userRole);
    
        userAuthorityRepository.save(userAuthority);
    
        return savedUser;
    }
    
    
    public Optional<User> findUser(String userId) {
        return userRepository.findByUserId(userId); //TODO 5 - Optional을 반환타입으로 하면 안좋다고 effective java에서 말한거같은데?
    }
    
    @Transactional
    public boolean updateUserName(String userId, String userName) {
        Optional<User> userOptional = userRepository.findByUserId(userId);
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
        Optional<User> userOptional = userRepository.findByUserId(userId);
        userOptional.ifPresent(user -> {
            if (user.getUserAuthorities() == null) {
                user.setUserAuthorities(new HashSet<>()); // Initialize if null
            }
    
            // Create and save the user's authority
            Authority userRole = authorityRepository.findByAuthority(Authority.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
    
            UserAuthority userAuthority = new UserAuthority();
            userAuthority.setUser(user);
            userAuthority.setAuthority(userRole);
    
            userAuthorityRepository.save(userAuthority);
            
            user.getUserAuthorities().add(userAuthority);
            user.setUpdated(LocalDateTime.now());
            userRepository.save(user);
        });
        return userOptional.isPresent();
    }
    
    @Transactional
    public boolean removeAuthority(String userId, String authority) {
        Optional<User> userOptional = userRepository.findByUserId(userId);
        userOptional.ifPresent(user -> {
            user.getUserAuthorities().remove(new Authority(authority)); //TODO - 검증: 정말 authority가 지워지는지 확인하기
            user.setUpdated(LocalDateTime.now());
            userRepository.save(user);
        });
        return userOptional.isPresent();
    }
    
    public void clearUsers() {
        userRepository.deleteAll();
    }
    
    public Page<User> listUsers(Integer page, Integer size) {
        return userRepository.findAll(PageRequest.of(page - 1, size));
    }
    
//    public Map<String, User> getUserMap(Collection<String> userIds) {
//        if (userIds == null || userIds.isEmpty()) {
//            return new HashMap<>();
//        }
//        return StreamSupport.stream(userRepository.findAllById(userIds).spliterator(), false)
//            .collect(Collectors.toMap(User::getUsername, Function.identity()));
//    }
}