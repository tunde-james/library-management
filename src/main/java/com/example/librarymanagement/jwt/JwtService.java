package com.example.librarymanagement.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import com.example.librarymanagement.config.JwtProperties;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String extractUsername(String jwtToken) {

        return extractClaim(jwtToken, Claims::getSubject);
    }

    private <T> T extractClaim(String jwtToken,
        Function<Claims, T> claimResolver) {

        final Claims claims = extractAllClaims(jwtToken);

        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String jwtToken) {

        return Jwts
            .parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(jwtToken)
            .getPayload();
    }

    public SecretKey getSignInKey() {

        return Keys
            .hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails userDetails) {

        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extractClaims,
        UserDetails userDetails) {

        return Jwts
            .builder()
            .claims(extractClaims)
            .subject(userDetails.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis()
                + jwtProperties.getExpiration().toMillis()))
            .signWith(getSignInKey())
            .compact();
    }

    public boolean isTokenValid(String jwtToken, UserDetails userDetails) {

        final String username = extractUsername(jwtToken);

        return (userDetails.getUsername().equals(username)
            && !isTokenExpired(jwtToken));
    }

    private boolean isTokenExpired(String jwtToken) {

        return extractExpiration(jwtToken).before(new Date());
    }

    private Date extractExpiration(String jwtToken) {

        return extractClaim(jwtToken, Claims::getExpiration);
    }
}
