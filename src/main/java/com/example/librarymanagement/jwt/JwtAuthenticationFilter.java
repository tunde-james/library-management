package com.example.librarymanagement.jwt;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;

import com.example.librarymanagement.exception.ErrorResponse;
import com.example.librarymanagement.service.TokenBlacklistService;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService,
            TokenBlacklistService tokenBlacklistService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwtToken = authHeader.substring(7);

        if (tokenBlacklistService.isBlacklisted(jwtToken)) {

            writeJsonError(response, request, "Token has been invalidated");

            return;
        }

        try {

            final String username = jwtService.extractUsername(jwtToken);

            if (username != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(jwtToken, userDetails)) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null,
                                    userDetails.getAuthorities());

                    authToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                }
            }
        } catch (JwtException ex) {

            writeJsonError(response, request, "Invalid or expired JWT");

            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeJsonError(HttpServletResponse response, HttpServletRequest request,
            String message) throws IOException {

        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized", message, request.getRequestURI());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }


}
