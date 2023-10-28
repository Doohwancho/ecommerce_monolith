package com.cho.ecommerce.domain.member.mapper;

import com.cho.ecommerce.api.domain.RegisterPostDTO;
import com.cho.ecommerce.domain.member.entity.AddressEntity;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    
    private final BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    public UserMapper(@Lazy BCryptPasswordEncoder passwordEncoder) { //added @Lazy to avoid circular reference
        this.passwordEncoder = passwordEncoder;
    }
    
    public UserEntity dtoToEntityWithNestedAddress(RegisterPostDTO dto, String role) {
        UserEntity userEntity = new UserEntity();
        
        userEntity.setUserId(dto.getUserId());
        userEntity.setEmail(dto.getEmail());
        userEntity.setName(dto.getName());
        userEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
    
        // Map Address
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setStreet(dto.getAddress().getStreet());
        addressEntity.setCity(dto.getAddress().getCity());
        addressEntity.setState(dto.getAddress().getState());
        addressEntity.setCountry(dto.getAddress().getCountry());
        addressEntity.setZipCode(dto.getAddress().getZipCode());
        
        // Set bidirectional relationship
        userEntity.setAddress(addressEntity);
        addressEntity.setUser(userEntity);
        
        // Setting default values
        userEntity.setCreated(LocalDateTime.now());
        userEntity.setUpdated(LocalDateTime.now());
        userEntity.setRole(role);
        userEntity.setEnabled(true);
        
        return userEntity;
    }
}
