package com.cho.ecommerce.global.config.security.handler;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
        org.springframework.security.core.Authentication authentication) throws IOException {
        // 현재 요청의 SecurityContext를 제거
        SecurityContextHolder.clearContext();
        
        // 로그아웃 후 리다이렉트
        response.sendRedirect("/login?logout");
    }
}
