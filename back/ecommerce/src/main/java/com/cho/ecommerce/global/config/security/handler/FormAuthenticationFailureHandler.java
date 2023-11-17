package com.cho.ecommerce.global.config.security.handler;

import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.repository.UserRepository;
import com.cho.ecommerce.domain.member.service.UserService;
import com.cho.ecommerce.global.error.ErrorCode;
import com.cho.ecommerce.global.error.exception.business.ResourceNotFoundException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FormAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    
    private final Logger log = LoggerFactory.getLogger(FormAuthenticationSuccessHandler.class);
    private final UserService userService;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException, ServletException {
        logger.info("여기에오! authentication failed!");
        
        String username = request.getParameter("username");
        UserEntity user = userRepository.findByUsername(username);
        
        if (user == null) {
            log.error("onAuthenticationFailure() failed! username: " + username);
            throw new ResourceNotFoundException(
                ErrorCode.RESOURCE_NOT_FOUND);
        }
        userService.incrementFailedAttempts(user);
        
        super.onAuthenticationFailure(request, response, exception);
    }
    
}
