package com.example.librarymanagement.config;

import java.io.IOException;
import java.time.Duration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

import com.example.librarymanagement.exception.ErrorResponse;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Cache<String, Bucket> buckets;
    private final ObjectMapper objectMapper;

    public RateLimitingFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.buckets = Caffeine.newBuilder().maximumSize(10_000)
                .expireAfterAccess(Duration.ofMinutes(10)).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (!path.equals("/api/v1/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = request.getRemoteAddr();
        Bucket bucket = buckets.get(clientIp, k -> createBucket());

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.TOO_MANY_REQUESTS.value(),
                    "Too Many Requests", "Too many login attempts. Please try again later.",
                    request.getRequestURI());

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Retry-After", "60");
            objectMapper.writeValue(response.getOutputStream(), errorResponse);
        }
    }

    private Bucket createBucket() {
        return Bucket.builder().addLimit(
                Bandwidth.builder().capacity(5).refillGreedy(5, Duration.ofMinutes(1)).build())
                .build();
    }

}
