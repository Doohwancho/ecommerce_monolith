package com.cho.ecommerce.controller;

import com.cho.ecommerce.dto.SecurityMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @RequestMapping("/")
    public String home() {
        return "index";
    }
    
    @RequestMapping("/login")
    public String login(
        @RequestParam(defaultValue = "false") Boolean error,
        Model model
    ) {
        if (error) {
            model.addAttribute("errorMessage", "아이디나 패스워드가 올바르지 않습니다.");
        }
        return "loginForm";
    }
    
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping(value = "/user")
    public SecurityMessage user() {
        return SecurityMessage.builder()
            .message("user page")
            .auth(SecurityContextHolder.getContext().getAuthentication()).build();
    }
    
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/admin")
    public SecurityMessage admin() {
        return SecurityMessage.builder()
            .message("admin page")
            .auth(SecurityContextHolder.getContext().getAuthentication()).build();
    }
}
