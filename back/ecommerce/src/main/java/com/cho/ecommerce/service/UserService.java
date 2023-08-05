package com.cho.ecommerce.service;

import com.cho.ecommerce.domain.Authority;
import com.cho.ecommerce.domain.User;
import com.cho.ecommerce.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Transactional
    public User save(User user) {
        if (user.getUserId() == null) {
            user.setCreated(LocalDateTime.now());
        }
        user.setUpdated(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    
    public Optional<User> findUser(String userId) {
        return userRepository.findById(userId); //TODO 5 - Optional을 반환타입으로 하면 안좋다고 effective java에서 말한거같은데?
    }
    
    @Transactional
    public boolean updateUserName(String userId, String userName) {
        Optional<User> userOptional = userRepository.findById(userId);
        userOptional.ifPresent(user -> {
            user.setName(userName);
            user.setUpdated(LocalDateTime.now());
            userRepository.save(user);
        });
        return userOptional.isPresent();
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException(username + "이 존재하지 않음"));
    }
    
    @Transactional
    public boolean addAuthority(String userId, String authority) {
        Optional<User> userOptional = userRepository.findById(userId);
        userOptional.ifPresent(user -> {
            if (user.getAuthorities() == null) {
                user.setAuthorities(new HashSet<>()); // Initialize if null
            }
            user.getAuthorities().add(new Authority(authority));
            user.setUpdated(LocalDateTime.now());
            userRepository.save(user);
        });
        return userOptional.isPresent();
    }
    
    @Transactional
    public boolean removeAuthority(String userId, String authority) {
        Optional<User> userOptional = userRepository.findById(userId);
        userOptional.ifPresent(user -> {
            user.getAuthorities().remove(new Authority(authority));
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
    
    public Map<String, User> getUserMap(Collection<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new HashMap<>();
        }
        return StreamSupport.stream(userRepository.findAllById(userIds).spliterator(), false)
            .collect(Collectors.toMap(User::getUserId, Function.identity()));
    }
}