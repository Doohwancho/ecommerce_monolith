package com.cho.ecommerce.domain.member.adapter;

import com.cho.ecommerce.api.domain.RegisterRequestDTO;
import com.cho.ecommerce.domain.member.domain.User;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.mapper.UserMapper;
import com.cho.ecommerce.domain.member.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserAdapter {
    private final UserMapper userMapper;
    private final UserService userService;
    
    public void saveRoleUser(RegisterRequestDTO registerRequestDTO) {
        UserEntity userEntity = userMapper.dtoToEntityWithNestedAddress(registerRequestDTO,
            "ROLE_USER");
    
        userService.saveRoleUser(userEntity);
    }
    
    public com.cho.ecommerce.api.domain.UserDetailsResponseDTO findUserDetailsDTOByUsername(
        String username) {
        User user = userService.findUserByUsername(username);
        return userMapper.toUserDetailsDTO(user);
    }
}
