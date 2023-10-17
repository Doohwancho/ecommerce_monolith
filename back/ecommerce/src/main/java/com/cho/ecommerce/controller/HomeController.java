package com.cho.ecommerce.controller;

import com.cho.ecommerce.domain.User;
import com.cho.ecommerce.dto.SecurityMessage;
import com.cho.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Controller
public class HomeController {
    
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final UserService userService;
    
    @RequestMapping("/")
    public String home() {
        return "index";
    }
    
    @GetMapping("/login")
    public String getLogin(
        @RequestParam(defaultValue = "false") Boolean error,
        Model model
    ) {
        if (error) {
            model.addAttribute("errorMessage", "아이디나 패스워드가 올바르지 않습니다.");
        }
        return "loginForm";
    }
    
    
    @PostMapping("/login")
    public String postLogin(
        @RequestParam(defaultValue = "false") Boolean error,
        Model model
    ) {
        if (error) {
            model.addAttribute("errorMessage", "아이디나 패스워드가 올바르지 않습니다.");
        }
        return "loginForm";
    }
    
    
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, BindingResult result) {
        if (result.hasErrors()) {
            // handle errors, maybe return to the registration page with error messages
            log.info("binding error on controller!!"); //TODO - binding result handling error 처리
            return "redirect:/register";
        }
        //@ModelAttribute User user
        userService.save(user);
        return "redirect:/login";
    }
//
//    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
//    @GetMapping(value = "/user")
//    public SecurityMessage user() {
//        return SecurityMessage.builder()
//            .message("user page")
//            .auth(SecurityContextHolder.getContext().getAuthentication()).build();
//    }
//
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
//    @GetMapping(value = "/admin")
//    public SecurityMessage admin() {
//        return SecurityMessage.builder()
//            .message("admin page")
//            .auth(SecurityContextHolder.getContext().getAuthentication()).build();
//    }
}
