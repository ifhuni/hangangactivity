package com.climbers.hangangactivity.security;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;


@Component
public class JwtUtil {
    private static final String SECRET_KEY = "mySecretKeyForJwtTokenGenerationWhichIsVerySecure";
    private static final long EXPIRATION_TIME = 86400000; // 1일

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}
