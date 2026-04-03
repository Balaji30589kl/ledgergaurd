package com.balaji.ledgerguard.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CurrentUserAuthenticationFilter currentUserAuthenticationFilter
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/error", "/h2-console/**").permitAll()
                        .requestMatchers("/api/users", "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/records", "/api/records/**").hasAnyRole("ANALYST", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/records").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/records/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/records/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .addFilterBefore(currentUserAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
