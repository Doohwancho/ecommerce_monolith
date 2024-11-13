package com.cho.ecommerce.global.config.security;


import com.cho.ecommerce.global.config.security.filter.IpAuthenticationFilter;
import com.cho.ecommerce.global.config.security.filter.RequestRateLimitFilter;
import com.cho.ecommerce.global.config.security.handler.CustomLogoutSuccessHandler;
import com.cho.ecommerce.global.config.security.handler.FormAuthenticationFailureHandler;
import com.cho.ecommerce.global.config.security.handler.FormAuthenticationSuccessHandler;
import com.cho.ecommerce.global.config.security.session.SecuritySessionExpiredStrategy;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig<S extends Session> extends WebSecurityConfigurerAdapter {
    
    private final UserDetailsService userDetailsService;
    private final SpringSessionBackedSessionRegistry<?> sessionRegistry;
    private final SecuritySessionExpiredStrategy securitySessionExpiredStrategy;
    private final FormAuthenticationSuccessHandler formSuccessHandler;
    private final FormAuthenticationFailureHandler formFailureHandler;
    private final RequestRateLimitFilter requestRateLimitFilter;
    private final IpAuthenticationFilter ipAuthenticationFilter;
    
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .cors().configurationSource(corsConfigurationSource()).and()
            .csrf()
            .ignoringAntMatchers("/h2-console/**") // Disable CSRF for H2 console
            .disable() //disable csrf for conveniency
            .headers()
            .frameOptions().disable() //h2-console 접속시 ui error 막기 위해 썼다.
            .xssProtection()
            .disable() //prevent Spring Security from adding the X-XSS-Protection header to the response, for spring security test
            .and()
            .addFilterBefore(requestRateLimitFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(ipAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(s -> s
                    .maximumSessions(
                        1) //동일 세션 개수 제한 => 1개로 설정하여 중복 로그인 방지 (localhost:8080에서 로그인하고, localhost:8081로 로그인 시도하면 http status 401 UNAUTHORIZED error 뜬다.
                    .sessionRegistry(sessionRegistry)
                    .maxSessionsPreventsLogin(
                        true) // true : 먼저 사용 중인 사용자의 세션이 유지되며, 새로 접속 한 사람은 로그인이 되지 않음. false: it expires the oldest session
                    /**
                     * Q. maxSessionsPreventsLogin() 가 true vs false?
                     *
                     * A. true: 보안상 더 좋다. false: 사용자 경험이 더 좋다.
                     *
                     * true가 비정상적인 로그인 시도를 차단하니까, 보안상 더 좋지만,
                     * 새로운 기기에서 로그인 시도가 막히므로 기존 기기에서 로그아웃 한 후 새 기기에서 다시 로그인 해야 하니까 불편하다.
                     */
                    .expiredSessionStrategy(securitySessionExpiredStrategy) //Session 만료됐을 때 가져갈 전략 설정
//                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // This is the default, but just to be explicit
                    .and()
                    .sessionAuthenticationErrorUrl("/login")
                //Specifies the URL to redirect to if an authentication error occurs due to maximum sessions.
//                .invalidSessionUrl("/login") //fix error: redirect를 20번 이상해서 주석처리 했음. Specifies the URL to redirect to if the session is invalid.
            )
            .authorizeRequests(f -> f
                    .antMatchers("/login").permitAll()
                    .antMatchers("/register").permitAll()
                    .antMatchers("/api/verification/**").permitAll()
                    .antMatchers("/products/**").permitAll()
                    .antMatchers("/categories/**").permitAll()
                    .antMatchers("/h2-console/**").permitAll() //allow h2-console access for developer
                    .antMatchers("/actuator/health").permitAll() //allow h2-console access for developer
                    .antMatchers("/actuator/prometheus").permitAll() //allow prometheus for monitoring
                    .antMatchers("/bulkinsert/**").permitAll() //allow prometheus for monitoring
                    .anyRequest().authenticated()
                //Q. what is .anyRequest().authenticated()?
                //any request not matched by the previous antMatchers should be authenticated.
                //In other words, all other URLs in your application require the user to be authenticated.
            )
            .formLogin(f -> f
                    .loginProcessingUrl("/login") //TODO - '/users/login'으로 추후 변경
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .successHandler(formSuccessHandler)
                    .failureHandler(formFailureHandler)
//                    .loginPage("/login") //custom page 구현한 경우. 없으면 default login page로 이동시킨다.
//                    .defaultSuccessUrl("/", true) //react의 navigate("/") 쓰기 때문에 주석처리
            )
            .logout(logout -> logout
                .logoutUrl("/logout") // URL to trigger logout
                .logoutSuccessUrl("/login?logout") // URL to redirect after logout
                .invalidateHttpSession(true) // Invalidate the session
                .deleteCookies("JSESSIONID") // Delete cookies on logout
                .logoutSuccessHandler(
                    new CustomLogoutSuccessHandler())); //custom handler에서 현재 요청의 인증 정보를 명시적으로 제거
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
    
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    
    // CORS configuration for local react-vite development
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration();
        
        configuration.setAllowedOrigins(
            Arrays.asList(
                "http://127.0.0.1:5173",
                "http://localhost:5173",
                "http://127.0.0.1:3000",
                "http://localhost:3000")); // Or use "*" for all origins (로컬 react-vite app의 포트가 5173이라 이 포트에서 오는 요청을 허용해준다.)
        configuration.setAllowedMethods(
            Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(
            Arrays.asList("Content-Type", "Authorization", "Accept", "X-Requested-With",
                "Access-Control-Allow-Headers", "Origin", "Cache-Control", "Pragma", "Expires",
                "X-Forwarded-For", "X-Forwarded-Host", "X-Forwarded-Proto"));
        configuration.setAllowCredentials(
            true); // Important for cookies like JSESSIONID, authorization headers with HTTPS
        //configuration.setAllowCredentials(true); 를 하면, .setAllowedOrigins("*"); ,.setAllowedHeaders("*") 가 시스템적으로 안되게 막혀있다. 꼭 특정 url, type으로 명시해야 한다.
        
        configuration.setMaxAge(
            3600L); //This sets the maximum age of the preflight request to 1 hour, which can help reduce the number of preflight requests.
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}