package com.cho.ecommerce.domain.member.controller;

import com.cho.ecommerce.api.UserApi;
import com.cho.ecommerce.api.domain.InlineResponse200;
import com.cho.ecommerce.api.domain.LoginPostDTO;
import com.cho.ecommerce.api.domain.RegisterPostDTO;
import com.cho.ecommerce.api.domain.RegisterResponseDTO;
import com.cho.ecommerce.domain.member.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
public class UserController implements UserApi {
    
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final UserService userService;
    
    private final AuthenticationManager authenticationManager;
    
    
    
//    @GetMapping("/login")
//    public String getLogin(
//        @RequestParam(defaultValue = "false") Boolean error,
//        Model model
//    ) {
//        if (error) {
//            model.addAttribute("errorMessage", "아이디나 패스워드가 올바르지 않습니다.");
//        }
//        return "loginForm";
//    }
    
    
    @Override
    public ResponseEntity<InlineResponse200> loginUser(@RequestBody LoginPostDTO loginPostDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginPostDTO.getUserId(),
                    loginPostDTO.getPassword()
                )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            InlineResponse200 response = new InlineResponse200();
            response.setMessage("Logged in successfully");
            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            
            InlineResponse200 response = new InlineResponse200();
            response.setMessage("Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            
        }
    }

//    @GetMapping("/register")
//    public String showRegistrationForm(Model model) {
//        model.addAttribute("user", new UserEntity());
//        return "register";
//    }
    
    
    @Override
    public ResponseEntity<RegisterResponseDTO> registerRoleUser(RegisterPostDTO registerPostDTO) {
        try {
            userService.saveRoleUser(registerPostDTO);
    
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
    public String user() {
        return "pageUser";
    }
//
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
//    @GetMapping(value = "/admin")
//    public String admin() {
//        return "pageAdmin";
//    }
}
