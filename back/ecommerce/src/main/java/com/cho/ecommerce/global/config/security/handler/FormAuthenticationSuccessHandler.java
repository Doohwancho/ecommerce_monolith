package com.cho.ecommerce.global.config.security.handler;

import java.io.IOException;
import java.util.Collection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        logger.info("authentication succeeded!!!!");
    
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(60 * 30); //30 min
        
        response.setHeader("login-status", "success");
        response.setHeader("Access-Control-Expose-Headers", "Login-Status"); //CORS때문에 js에서 login-status header를 extract 못하니까, Access-Control-Expose-Headers 로 'login' header를 expose 해준다.
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
//    //case1) ROLE에 따라 다른 페이지로 redirect
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//        Authentication authentication) throws IOException, ServletException {
//        String redirectUrl = "/";
//        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//
//        for (GrantedAuthority grantedAuthority : authorities) {
//
//            if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
//                redirectUrl = "/admin";
//                break;
//            } else if (grantedAuthority.getAuthority().equals("ROLE_USER")) {
//                redirectUrl = "/user";
//                break;
//            }
//        }
//        redirectStrategy.sendRedirect(request, response, redirectUrl);
//    }
   
    //case2) session에 필드 추가
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        HttpSession session = request.getSession();
//        session.setAttribute("welcomeMessage", "Welcome back, " + authentication.getName() + "!");
//        super.onAuthenticationSuccess(request, response, authentication);
//    }
    
    //case3) storing last login timestamp
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
