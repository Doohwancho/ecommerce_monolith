package com.cho.ecommerce.domain.member.service;

import com.cho.ecommerce.domain.member.entity.AuthorityEntity;
import com.cho.ecommerce.domain.member.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorityService {
    
    @Autowired
    private AuthorityRepository authorityRepository;
    
    public AuthorityEntity findByAuthority(String authority) {
        return authorityRepository.findByAuthority(authority).orElse(null);
    }
}