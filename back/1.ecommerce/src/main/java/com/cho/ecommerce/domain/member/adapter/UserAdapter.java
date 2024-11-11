package com.cho.ecommerce.domain.member.adapter;

import com.cho.ecommerce.api.domain.RegisterRequestDTO;
import com.cho.ecommerce.domain.member.domain.User;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.mapper.UserMapper;
import com.cho.ecommerce.domain.member.service.UserService;
import com.cho.ecommerce.domain.member.service.UserVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserAdapter {
    
    private final UserMapper userMapper;
    private final UserService userService;
    private final UserVerificationService userVerificationService;
    
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
    
    public boolean findUserExistsByUserId(String userId) {
        return userVerificationService.findUserExistsByUserId(userId);
    }
    
    public void sendVerificationCode(String userId) {
        userVerificationService.sendVerificationCodeByUserId(userId);
    }
    
    public boolean verifyCode(String userId, String code) {
        return userVerificationService.verifyCode(userId, code);
    }
}
