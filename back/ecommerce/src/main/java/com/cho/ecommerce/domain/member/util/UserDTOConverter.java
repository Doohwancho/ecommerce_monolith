package com.cho.ecommerce.domain.member.util;

import com.cho.ecommerce.api.domain.RegisterPostDTO;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.repository.AuthorityRepository;
import com.cho.ecommerce.domain.member.repository.UserAuthorityRepository;
import com.cho.ecommerce.domain.member.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserDTOConverter {
    
    private final BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    public UserDTOConverter(@Lazy BCryptPasswordEncoder passwordEncoder) { //added @Lazy to avoid circular reference
        this.passwordEncoder = passwordEncoder;
    }
    
    public UserEntity dtoToEntity(RegisterPostDTO dto) {
        UserEntity userEntity = new UserEntity();
        
        userEntity.setUserId(dto.getUserId());
        userEntity.setEmail(dto.getEmail());
        userEntity.setName(dto.getName());
        userEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
        
        // Setting default values
        userEntity.setCreated(LocalDateTime.now());
        userEntity.setUpdated(LocalDateTime.now());
        userEntity.setRole("USER");
        userEntity.setEnabled(true);
        
        return userEntity;
    }
}
