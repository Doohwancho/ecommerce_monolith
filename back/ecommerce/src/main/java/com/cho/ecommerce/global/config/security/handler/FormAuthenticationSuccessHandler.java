package com.cho.ecommerce.global.config.security.handler;

import java.io.IOException;
import java.util.Collection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FormAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    private final Logger logger = LoggerFactory.getLogger(FormAuthenticationSuccessHandler.class);
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    
    //case1) ROLE에 따라 다른 페이지로 redirect
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        String redirectUrl = "/";
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        for (GrantedAuthority grantedAuthority : authorities) {
            
            if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
                redirectUrl = "/admin";
                break;
            } else if (grantedAuthority.getAuthority().equals("ROLE_USER")) {
                redirectUrl = "/user";
                break;
            }
        }
        redirectStrategy.sendRedirect(request, response, redirectUrl);
    }
    
    //case2) logging successful login
//    private final Logger logger = LoggerFactory.getLogger(FormAuthenticationSuccessHandler.class);
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        logger.info("User {} logged in successfully", authentication.getName());
//        super.onAuthenticationSuccess(request, response, authentication);
//    }
    
    //case3) session에 필드 추가
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        HttpSession session = request.getSession();
//        session.setAttribute("welcomeMessage", "Welcome back, " + authentication.getName() + "!");
//        super.onAuthenticationSuccess(request, response, authentication);
//    }
    
    //case4) storing last login timestamp
//    private final UserRepository userRepository;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        User user = userRepository.findByUsername(authentication.getName());
//        user.setLastLoginTimestamp(LocalDateTime.now());
//        userRepository.save(user);
//        super.onAuthenticationSuccess(request, response, authentication);
//    }

}
