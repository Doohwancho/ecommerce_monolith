package com.cho.ecommerce.service;

import com.cho.ecommerce.domain.Authority;
import com.cho.ecommerce.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorityService {
    
    @Autowired
    private AuthorityRepository authorityRepository;
    
    public Authority findByAuthority(String authority) {
        return authorityRepository.findByAuthority(authority).orElse(null);
    }
}