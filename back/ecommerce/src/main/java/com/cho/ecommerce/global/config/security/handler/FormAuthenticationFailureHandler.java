package com.cho.ecommerce.global.config.security.handler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FormAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    
    
    private final Logger logger = LoggerFactory.getLogger(FormAuthenticationSuccessHandler.class);
    
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException, ServletException {
        logger.info("여기에오!!! on auth failure!");
    }
    
    //case1) auth fail시 error message 보내기 위함
//    @Override
//    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
//        logger.info("on auth failure!");
//
//        String errorMessage = "Authentication Failed!";
//        String userId = request.getParameter("userId");
//
//        request.setAttribute("userId", userId); // Login 화면에서 사용자가 입력한 user Id를 유지하기 위함
//        request.setAttribute("exception", errorMessage);
//
//        request.getRequestDispatcher("/login").forward(request, response);
//    }
    
    //case2) logging failed logins
//    @Override
//    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
//        log.warn("Failed login attempt by user: {}", email);
//        super.onAuthenticationFailure(request, response, exception);
//    }
    
    //case3) account lock
//    private final UserRepository userRepository;
//
//    @Override
//    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
//        User user = userRepository.findByEmail(email);
//        if (user != null) {
//            user.incrementFailedLoginAttempts();
//            if (user.getFailedLoginAttempts() >= MAX_ATTEMPTS) {
//                user.lockAccount();
//            }
//            userRepository.save(user);
//        }
//        super.onAuthenticationFailure(request, response, exception);
//    }


}
