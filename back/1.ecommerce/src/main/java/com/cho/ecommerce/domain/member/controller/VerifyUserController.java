package com.cho.ecommerce.domain.member.controller;

import com.cho.ecommerce.domain.member.adapter.UserAdapter;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class VerifyUserController implements com.cho.ecommerce.api.UserApi {
    
    private final UserAdapter userAdapter;
    
    @Override
    public ResponseEntity<String> verifyUserExists(@Valid @RequestBody
    com.cho.ecommerce.api.domain.VerifyUserIdDTO verifyUserIdDTO) {
        try {
            boolean userExists = userAdapter.findUserExistsByUserId(verifyUserIdDTO.getUserId());
            
            if (userExists) {
                return new ResponseEntity<>(new String("User Exists!"),
                    HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(new String("User Does Not Exists!"),
                    HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Override
    public ResponseEntity<String> sendVerificationCode(@Valid @RequestBody
    com.cho.ecommerce.api.domain.VerifyUserIdDTO verifyUserIdDTO) {
        try {
            userAdapter.sendVerificationCode(verifyUserIdDTO.getUserId());
            return ResponseEntity.ok("Verification code sent successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Failed to send verification code: " + e.getMessage());
        }
    }
    
    @Override
    public ResponseEntity<String> verifyCode(
        @Valid @RequestBody com.cho.ecommerce.api.domain.VerificationRequestDTO verificationRequestDTO) {
        
        try {
            boolean verified = userAdapter.verifyCode(verificationRequestDTO.getUserId(),
                verificationRequestDTO.getCode());
            
            if (verified) {
                return new ResponseEntity<>(new String("Verified!"),
                    HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(new String("Failed to Verify!"),
                    HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Override
    public ResponseEntity<Void> resetPassword(
        @Valid @RequestBody com.cho.ecommerce.api.domain.PasswordResetRequestDTO request) {
        userAdapter.setPasswordAfterVerification(
            request.getUserId(),
            request.getNewPassword()
        );
        return ResponseEntity.ok().build();
    }
}
