package com.cho.ecommerce.domain.member.mapper;

import com.cho.ecommerce.api.domain.AddressDTO;
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
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    
    public UserEntity dtoToEntityWithNestedAddress(
        com.cho.ecommerce.api.domain.RegisterRequestDTO dto, String role) {
        UserEntity userEntity = new UserEntity();
        
        userEntity.setUsername(dto.getUsername());
        userEntity.setEmail(dto.getEmail());
        userEntity.setName(dto.getName());
        userEntity.setPassword(dto.getPassword());
        
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
        userEntity.setFailedAttempt(0);
        
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
    
    public com.cho.ecommerce.api.domain.UserDetailsResponseDTO toUserDetailsDTO(User user) {
        if (user == null) {
            return null;
        }
        
        com.cho.ecommerce.api.domain.UserDetailsResponseDTO userDetailsResponseDTO = new com.cho.ecommerce.api.domain.UserDetailsResponseDTO();
        userDetailsResponseDTO.setUsername(user.getUsername());
        userDetailsResponseDTO.setEmail(user.getEmail());
        userDetailsResponseDTO.setName(user.getName());
        userDetailsResponseDTO.setAddress(toAddressDTO(user.getAddress()));
        userDetailsResponseDTO.setRole(user.getRole());
        userDetailsResponseDTO.setEnabled(user.getEnabled());
        userDetailsResponseDTO.setCreated(user.getCreated());
        userDetailsResponseDTO.setUpdated(user.getUpdated());
        userDetailsResponseDTO.setAuthorities(user.getAuthorities());
        
        return userDetailsResponseDTO;
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
