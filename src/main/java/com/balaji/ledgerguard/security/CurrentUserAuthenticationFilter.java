package com.balaji.ledgerguard.security;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.balaji.ledgerguard.entity.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CurrentUserAuthenticationFilter extends OncePerRequestFilter {

    private static final String USER_ID_HEADER = "X-USER-ID";

    private final CurrentUserService currentUserService;

    public CurrentUserAuthenticationFilter(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String userIdHeader = request.getHeader(USER_ID_HEADER);

        if (userIdHeader == null || userIdHeader.isBlank()) {
            writeProblemDetail(response, HttpStatus.UNAUTHORIZED, "Unauthorized", "X-USER-ID header is required");
            return;
        }

        User currentUser = currentUserService.resolveActiveUser(userIdHeader.trim());
        if (currentUser == null) {
            writeProblemDetail(response, HttpStatus.UNAUTHORIZED, "Unauthorized", "Current user not found");
            return;
        }

        if (currentUser.getStatus() != com.balaji.ledgerguard.enums.UserStatus.ACTIVE) {
            writeProblemDetail(response, HttpStatus.FORBIDDEN, "Forbidden", "Current user is inactive");
            return;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                currentUser.getId(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + currentUser.getRole().name()))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        try {
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private void writeProblemDetail(HttpServletResponse response, HttpStatus status, String title, String detail)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/problem+json");
        String safeDetail = detail.replace("\"", "\\\"");
        String body = "{" +
                "\"type\":\"https://ledgerguard.example/" + status.value() + "\"," +
                "\"title\":\"" + title + "\"," +
                "\"status\":" + status.value() + "," +
                "\"detail\":\"" + safeDetail + "\"," +
                "\"timestamp\":\"" + Instant.now() + "\"" +
                "}";
        response.getWriter().write(body);
    }
}
