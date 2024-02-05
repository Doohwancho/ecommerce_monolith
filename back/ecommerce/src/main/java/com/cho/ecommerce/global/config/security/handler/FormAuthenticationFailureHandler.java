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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        
        //유저가 데이터베이스에 존재하는지 확인한다.
        String username = request.getParameter("username");
        UserEntity user = userRepository.findByUsername(username);
        
        //없는 유저였다면, Exception을 던진다.
        if (user == null) {
//            log.info("onAuthenticationFailure() failed! username: " + username);
            throw new ResourceNotFoundException(
                ErrorCode.RESOURCE_NOT_FOUND);
        }
        
        // 개발 편의성을 위해 failed login request의 JSESSIONID를 꺼내 log 찍어본다.
//        Cookie[] cookies = request.getCookies();
//
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if ("JSESSIONID".equals(cookie.getName())) {
//                    logger.info("JSESSIONID: " + cookie.getValue());
//                    break;
//                }
//            }
//        }
        
        //있는 유저였는데 로그인 실패했다면, 비밀번호 실패했다는 말이니, 실패시도 +1을 한다. (5회 이상 실패하면 계정 잠김)
        userService.incrementFailedAttempts(user);
        
        response.setHeader("login-status", "failed");
        response.setHeader("Access-Control-Expose-Headers",
            "Login-Status"); //CORS때문에 js에서 login-status header를 extract 못하니까, Access-Control-Expose-Headers 로 'login' header를 expose 해준다.
        response.setStatus(HttpServletResponse.SC_OK);
//        super.onAuthenticationFailure(request, response, exception);
    }
    
}
