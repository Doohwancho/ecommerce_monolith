package com.cho.ecommerce.service;

import com.cho.ecommerce.domain.User;
import com.cho.ecommerce.domain.UserAuthority;
import com.cho.ecommerce.repository.UserAuthorityRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAuthorityService {
    @Autowired
    private UserAuthorityRepository userAuthorityRepository;
    
    public List<UserAuthority> findByUser(User user) {
        return userAuthorityRepository.findByUser(user);
    }
    
    public UserAuthority saveUserAuthority(UserAuthority userAuthority) {
        return userAuthorityRepository.save(userAuthority);
    }
}
