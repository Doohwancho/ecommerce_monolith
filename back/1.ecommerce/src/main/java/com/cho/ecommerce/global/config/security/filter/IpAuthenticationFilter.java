package com.cho.ecommerce.global.config.security.filter;

import com.cho.ecommerce.domain.member.service.UserAuthenticationService;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import java.io.IOException;
import java.net.InetAddress;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
public class IpAuthenticationFilter extends OncePerRequestFilter {
    
    private final DatabaseReader databaseReader;
    private final UserAuthenticationService userAuthenticationService;
    
    
    public IpAuthenticationFilter(DatabaseReader databaseReader,
        UserAuthenticationService userAuthenticationService) {
        this.databaseReader = databaseReader;
        this.userAuthenticationService = userAuthenticationService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain) throws ServletException, IOException {
        
        String ipAddress = getClientIp(request);
        InetAddress inetAddress = InetAddress.getByName(ipAddress);
        
        String country = null;
        try {
            // Handle localhost/development IPs
            if (inetAddress.isLoopbackAddress()) {
                chain.doFilter(request, response);
                return;
            }
            
            country = databaseReader.country(inetAddress).getCountry().getName();
            
            if (country == null || !country.equals("South Korea")) {
                // Get authentication info - might be null for unauthenticated requests
                Authentication authentication = SecurityContextHolder.getContext()
                    .getAuthentication();
                
                if (authentication != null && authentication.isAuthenticated() &&
                    !(authentication instanceof AnonymousAuthenticationToken)) {
                    String username = authentication.getName();
                    log.warn(
                        "Authenticated user from non-KR IP - Username: {}, IP: {}, Country: {}",
                        username, ipAddress, country);
                    userAuthenticationService.invalidateUserSessionAndLockUser(username);
                } else {
                    // Just log and block unauthenticated requests
                    log.warn("Unauthenticated request from non-KR IP - IP: {}, Country: {}",
                        ipAddress, country);
                }
                
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Access denied: Only Korean IPs allowed");
                return;
            }
        } catch (GeoIp2Exception e) {
            log.error("GeoIP2 error for IP: " + ipAddress, e);
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Unable to verify location");
        }
        
        chain.doFilter(request, response);
    }
    
    private String getClientIp(HttpServletRequest request) {
        String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };
        
        for (String header : IP_HEADER_CANDIDATES) {
            String ipList = request.getHeader(header);
            if (ipList != null && ipList.length() != 0 && !"unknown".equalsIgnoreCase(ipList)) {
                String ip = ipList.split(",")[0];
                return ip;
            }
        }
        
        return request.getRemoteAddr();
    }
}
