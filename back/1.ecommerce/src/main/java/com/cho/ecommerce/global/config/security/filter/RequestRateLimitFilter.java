package com.cho.ecommerce.global.config.security.filter;

import com.cho.ecommerce.domain.member.service.UserAuthenticationService;
import com.google.common.util.concurrent.RateLimiter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class RequestRateLimitFilter extends OncePerRequestFilter {
    
    private final Map<String, RateLimiter> limiters = new ConcurrentHashMap<>();
    private final UserAuthenticationService userAuthenticationService;
    
    // Rate limit: 15 requests per second per user
    private static final double REQUESTS_PER_SECOND = 15.0;
    
    public RequestRateLimitFilter(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        
        String username = getCurrentUsername();
        if (username != null) {
            RateLimiter limiter = limiters.computeIfAbsent(username,
                k -> RateLimiter.create(REQUESTS_PER_SECOND));
            
            if (!limiter.tryAcquire()) {
                // Rate limit exceeded, track suspicious activity
                log.warn("Rate limit exceeded for user: {}. Locking account.", username);
                userAuthenticationService.invalidateUserSessionAndLockUser(username);
                limiters.remove(username);
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
            !(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getName();
        }
        return null;
    }
    
    //!isPublicEndpoint(request.getRequestURI()) 조건 추가할 수 있다.
//    private boolean isPublicEndpoint(String uri) {
//        return uri.startsWith("/login") ||
//            uri.startsWith("/register") ||
//            uri.startsWith("/products") ||
//            uri.startsWith("/categories") ||
//            uri.startsWith("/api/verification");
//    }
}