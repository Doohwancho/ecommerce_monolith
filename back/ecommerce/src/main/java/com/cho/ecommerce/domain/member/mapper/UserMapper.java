package com.cho.ecommerce.domain.member.mapper;

import com.cho.ecommerce.api.domain.AddressDTO;
import com.cho.ecommerce.api.domain.RegisterPostDTO;
import com.cho.ecommerce.api.domain.UserDetailsDTO;
import com.cho.ecommerce.domain.member.domain.Address;
import com.cho.ecommerce.domain.member.domain.User;
import com.cho.ecommerce.domain.member.entity.AddressEntity;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    
    private final BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    public UserMapper(
        @Lazy BCryptPasswordEncoder passwordEncoder) { //added @Lazy to avoid circular reference
        this.passwordEncoder = passwordEncoder;
    }
    
    public UserEntity dtoToEntityWithNestedAddress(RegisterPostDTO dto, String role) {
        UserEntity userEntity = new UserEntity();
        
        userEntity.setUsername(dto.getUsername());
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
    
    public User toUser(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        
        User user = new User();
        user.setMemberId(userEntity.getMemberId());
        user.setUsername(userEntity.getUsername());
        user.setEmail(userEntity.getEmail());
        user.setName(userEntity.getName());
        user.setAddress(toAddress(userEntity.getAddress()));
        user.setRole(userEntity.getRole());
        user.setEnabled(userEntity.isEnabled());
        user.setFailedAttempt(userEntity.getFailedAttempt());
        user.setCreated(convertToLocalDateTimeViaInstant(userEntity.getCreated()));
        user.setUpdated(convertToLocalDateTimeViaInstant(userEntity.getUpdated()));
        user.setAuthorities(userEntity.getUserAuthorities().stream()
            .map(auth -> auth.getAuthorityEntity().getAuthority())
            .collect(Collectors.toList()));
        
        return user;
    }
    
    public Address toAddress(AddressEntity addressEntity) {
        if (addressEntity == null) {
            return null;
        }
        
        Address address = new Address();
        address.setAddressId(addressEntity.getAddressId());
        address.setStreet(addressEntity.getStreet());
        address.setCity(addressEntity.getCity());
        address.setState(addressEntity.getState());
        address.setCountry(addressEntity.getCountry());
        address.setZipCode(addressEntity.getZipCode());
        
        return address;
    }
    
    private static OffsetDateTime convertToLocalDateTimeViaInstant(LocalDateTime dateToConvert) {
        return dateToConvert == null ? null
            : dateToConvert.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }
    
    public UserDetailsDTO toUserDetailsDTO(User user) {
        if (user == null) {
            return null;
        }
        
        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setUsername(user.getUsername());
        userDetailsDTO.setEmail(user.getEmail());
        userDetailsDTO.setName(user.getName());
        userDetailsDTO.setAddress(toAddressDTO(user.getAddress()));
        userDetailsDTO.setRole(user.getRole());
        userDetailsDTO.setEnabled(user.getEnabled());
        userDetailsDTO.setCreated(user.getCreated());
        userDetailsDTO.setUpdated(user.getUpdated());
        userDetailsDTO.setAuthorities(user.getAuthorities());
        
        return userDetailsDTO;
    }
    
    public AddressDTO toAddressDTO(Address address) {
        if (address == null) {
            return null;
        }
        
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setStreet(address.getStreet());
        addressDTO.setCity(address.getCity());
        addressDTO.setState(address.getState());
        addressDTO.setCountry(address.getCountry());
        addressDTO.setZipCode(address.getZipCode());
        
        return addressDTO;
    }
}
