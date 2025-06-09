package com.example.devFlow.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf().disable() // temporarily disable CSRF for testing
        .authorizeRequests(auth -> auth
            .requestMatchers(
                "/", "/notifications", "/custom_login", "/developers", "/clients",
                "/create_comment", "/update_profile_dev", "/view_profile_dev", "/update_profile", "/create_offer",
                "/images/**", "/css/**", "/js/**", "/webjars/**",
                "/project", "/custom_logout", "/projects", "/projects/**", "/create", "/create_dev", "/create_profile",
                "/create_project", "/client_dashboard", "/create_profile_dev", "/login_success", "/success",
                "/login_info", "/view_profile", "/register", "/signup", "/signup_info", "/check-email", "/check-username",
                "/check-email-login", "/check-password-login", "/index", "/view_client_profile", "/view_developer_profile",
                "/profile/**", "/devprofile/**","/offers/**"
            ).permitAll()
            .anyRequest().authenticated()
        )
        .formLogin().disable()
        .logout(logout -> logout.disable());

    return http.build();
}

}
