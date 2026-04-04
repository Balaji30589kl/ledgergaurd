package com.balaji.ledgerguard.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
                        .requestMatchers(HttpMethod.GET, "/api/dashboard/**").hasAnyRole("VIEWER", "ANALYST", "ADMIN")
                        .anyRequest().permitAll()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) ->
                                writeProblemDetail(response, HttpStatus.UNAUTHORIZED, "Unauthorized", authException.getMessage()))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeProblemDetail(response, HttpStatus.FORBIDDEN, "Forbidden", accessDeniedException.getMessage()))
                )
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .addFilterBefore(currentUserAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

        private void writeProblemDetail(
                        jakarta.servlet.http.HttpServletResponse response,
                        HttpStatus status,
                        String title,
                        String detail
        ) throws java.io.IOException {
                response.setStatus(status.value());
                response.setContentType("application/problem+json");
                String safeDetail = detail == null ? status.getReasonPhrase() : detail.replace("\"", "\\\"");
                String body = "{" +
                                "\"type\":\"https://ledgerguard.example/" + status.value() + "\"," +
                                "\"title\":\"" + title + "\"," +
                                "\"status\":" + status.value() + "," +
                                "\"detail\":\"" + safeDetail + "\"," +
                                "\"timestamp\":\"" + java.time.Instant.now() + "\"" +
                                "}";
                response.getWriter().write(body);
        }
}
