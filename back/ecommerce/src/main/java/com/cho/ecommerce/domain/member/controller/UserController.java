package com.cho.ecommerce.domain.member.controller;

import com.cho.ecommerce.api.UserApi;
import com.cho.ecommerce.api.domain.RegisterResponseDTO;
import com.cho.ecommerce.domain.member.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class UserController implements UserApi {
    
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final UserService userService;
    
    private final AuthenticationManager authenticationManager;
    
    
    //TODO - react app에 GET /login 페이지 생기면 삭제하기
    @GetMapping("/login")
    public String getLogin(@RequestParam(defaultValue = "false") Boolean error, Model model) {
        if (error) {
            model.addAttribute("errorMessage", "아이디나 패스워드가 올바르지 않습니다.");
        }
        return "loginForm";
    }
    
    @Override
    public ResponseEntity<RegisterResponseDTO> registerRoleUser(@Valid com.cho.ecommerce.api.domain.RegisterRequestDTO registerRequestDTO) {
        try {
            userService.saveRoleUser(registerRequestDTO);
            
            // Creating a response
            RegisterResponseDTO response = new RegisterResponseDTO();
            response.setMessage("Registration successful");
            
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            // Handle the exception, maybe return a 400 Bad Request or similar
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping(value = "/user")
    public ResponseEntity<RegisterResponseDTO> user() {
        RegisterResponseDTO response = new RegisterResponseDTO();
        response.setMessage("test user page! with ROLE_USER!");
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/admin")
    public String admin() {
        return "pageAdmin";
    }
    
    @Override
    public ResponseEntity<com.cho.ecommerce.api.domain.UserDetailsResponseDTO> getUserByUsername(@Valid @PathVariable String username) {
        com.cho.ecommerce.api.domain.UserDetailsResponseDTO userDetailsResponseDTOByUsername = userService.findUserDetailsDTOByUsername(
            username);
        
        return new ResponseEntity<>(userDetailsResponseDTOByUsername, HttpStatus.OK);
    }
}
