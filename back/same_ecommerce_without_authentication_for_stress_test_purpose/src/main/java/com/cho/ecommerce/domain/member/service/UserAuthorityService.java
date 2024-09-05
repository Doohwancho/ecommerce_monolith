package com.cho.ecommerce.domain.member.service;

import com.cho.ecommerce.domain.member.entity.UserAuthorityEntity;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.repository.UserAuthorityRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAuthorityService {
    
    @Autowired
    private UserAuthorityRepository userAuthorityRepository;
    
    public List<UserAuthorityEntity> findByUser(UserEntity userEntity) {
        return userAuthorityRepository.findByUserEntity(userEntity);
    }
    
    public UserAuthorityEntity saveUserAuthority(UserAuthorityEntity userAuthorityEntity) {
        return userAuthorityRepository.save(userAuthorityEntity);
    }
}
