package com.cho.ecommerce.global.config;


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

@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    private final UserDetailsService userDetailsService;
    
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
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // This is the default, but just to be explicit
            .and()
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
}