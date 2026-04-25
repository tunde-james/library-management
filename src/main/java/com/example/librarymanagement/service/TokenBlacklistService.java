package com.example.librarymanagement.service;

import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import com.example.librarymanagement.config.JwtProperties;

@Service
public class TokenBlacklistService {

    private final Cache<String, Boolean> blacklistedTokens;

    public TokenBlacklistService(JwtProperties jwtProperties) {
        this.blacklistedTokens = Caffeine
            .newBuilder()
            .expireAfterWrite(jwtProperties.getExpiration())
            .maximumSize(100_000)
            .build();
    }

    public void blacklistToken(String token) {

        blacklistedTokens.put(token, Boolean.TRUE);
    }

    public boolean isBlacklisted(String token) {

        return blacklistedTokens.getIfPresent(token) != null;
    }
}
