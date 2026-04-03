package com.balaji.ledgerguard.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "X-USER-ID header is required");
            return;
        }

        User currentUser = currentUserService.resolveActiveUser(userIdHeader.trim());
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Current user not found");
            return;
        }

        if (currentUser.getStatus() != com.balaji.ledgerguard.enums.UserStatus.ACTIVE) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Current user is inactive");
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
}
