package com.example.librarymanagement.config;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import jakarta.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Data
@Component
@Validated
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;
    private Duration expiration;

    @PostConstruct
    public void validate() {

        Assert.hasText(secret, "jwt.secret must be set and cannot be blank");
        Assert.isTrue(secret.getBytes(StandardCharsets.UTF_8).length >= 32,
                "jwt.secret must be at least 32 bytes (256 bits) to prevent WeakKeyException");
        Assert.notNull(expiration,
                "jwt.expiration must be set in application.yml e.g. 1d, 12h, 30m");
    }
}
