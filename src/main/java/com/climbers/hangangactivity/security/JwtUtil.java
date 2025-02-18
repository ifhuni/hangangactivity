package com.climbers.hangangactivity.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import org.springframework.security.core.Authentication;

@Component
public class JwtUtil {

    private final Key key;
    private final long expirationMs;

    // 생성자에서 secret 키와 expiration 시간을 주입받고, 비밀키를 생성
    public JwtUtil(@Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes()); // 비밀키 생성
        this.expirationMs = expirationMs; // 토큰 만료 시간 설정
    }

    // 이메일을 기반으로 JWT 토큰을 생성
    public String generateToken(Authentication authentication) {
        String email = authentication.getName(); // 인증된 사용자 이름(이메일) 가져오기
        return Jwts.builder()
                .setSubject(email) // 이메일을 subject로 설정
                .setIssuedAt(new Date()) // 토큰 발급 시간 설정
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs)) // 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS256) // 비밀키로 서명
                .compact(); // 토큰 생성
    }

    // JWT 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token); // 토큰 파싱 & 검증

            return true; // 검증 성공 시 true 반환
        } catch (JwtException e) {
            return false; // 토큰이 유효하지 않음
        }
    }

    // JWT 토큰에서 Subject(email) 추출
    public String getSubjectFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
