package com.cho.ecommerce.global.config.security.session;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class SessionConfig {
    
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("JSESSIONID"); // Default name is SESSION
        serializer.setCookiePath("/"); // Cookie path
        serializer.setDomainNamePattern("^.+?\\.(\\w+\\.[a-z]+)$"); // Domain pattern
        serializer.setUseHttpOnlyCookie(true); // Use HttpOnly flag for security
        serializer.setUseSecureCookie(true); // Use Secure flag for security
        serializer.setCookieMaxAge(1800); // 30 minutes
        return serializer;
    }
}
