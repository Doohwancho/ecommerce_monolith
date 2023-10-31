package com.cho.ecommerce.global.config;


import com.cho.ecommerce.global.config.session.SecuritySessionExpiredStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig<S extends Session> extends WebSecurityConfigurerAdapter {
    
    private final UserDetailsService userDetailsService;
    private final FindByIndexNameSessionRepository<S> sessionRepository;
    private final SecuritySessionExpiredStrategy securitySessionExpiredStrategy;
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf()
                .ignoringAntMatchers("/h2-console/**") // Disable CSRF for H2 console
                .disable() //disable csrf for conveniency
                .headers().frameOptions().disable() //h2-console 접속시 ui error 막기 위해 썼다.
            .and()
                .sessionManagement(s -> s
                    .maximumSessions(1) //동일 세션 개수 제한 => 1개로 설정하여 중복 로그인 방지
                    .sessionRegistry(sessionRegistry()) //정보 조사
                    .maxSessionsPreventsLogin(true) // true : 먼저 사용 중인 사용자의 세션이 유지되며, 새로 접속 한 사람은 로그인이 되지 않음
                    .expiredSessionStrategy(securitySessionExpiredStrategy)) //Session 만료됐을 때 가져갈 전략 설정
//                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // This is the default, but just to be explicit
            .formLogin(config -> {
                config.loginPage("/login")
                    .failureForwardUrl("/login?error=true")
                    .defaultSuccessUrl("/", true);
            })
            .authorizeRequests(config -> {
                config.antMatchers("/login").permitAll()
                    .antMatchers("/register").permitAll()
                    .antMatchers("/h2-console/**").permitAll() //allow h2-console access for developer
                    .anyRequest().authenticated();
                //Q. what is .anyRequest().authenticated()?
                //any request not matched by the previous antMatchers should be authenticated.
                //In other words, all other URLs in your application require the user to be authenticated.
            })
        ;
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
    
    // sessionRegistry 추가
    @Bean
    public SpringSessionBackedSessionRegistry<S> sessionRegistry() {
        return new SpringSessionBackedSessionRegistry<>(this.sessionRepository);
    }
}