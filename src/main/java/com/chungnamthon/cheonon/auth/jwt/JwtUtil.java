package com.chungnamthon.cheonon.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.SecretKey;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKeyString;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyString);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(Long userId) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public TokenWithExpiry createRefreshTokenWithExpiry(Long userId) {
        long nowMillis = System.currentTimeMillis();
        long expiryMillis = nowMillis + refreshExpiration;

        String token = Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(new Date(nowMillis))
                .expiration(new Date(expiryMillis))
                .signWith(getSigningKey())
                .compact();

        LocalDateTime expiry = Instant.ofEpochMilli(expiryMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        return new TokenWithExpiry(token, expiry);
    }

    // 토큰 전처리 로직
    public Long getUserIdFromToken(String token) {
        // 토큰 전처리
        token = preprocessToken(token);

        try {
            Jws<Claims> jwt = Jwts.parser()
                    .verifyWith((SecretKey) getSigningKey())
                    .build()
                    .parseSignedClaims(token);

            return Long.parseLong(jwt.getPayload().getSubject());
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT token. Token length: {}, First 50 chars: '{}'",
                    token.length(),
                    token.length() > 50 ? token.substring(0, 50) + "..." : token);
            throw e;
        }
    }

    //토큰 검증
    public boolean validateToken(String token) {
        try {
            // 토큰 전처리
            token = preprocessToken(token);

            Jwts.parser()
                    .verifyWith((SecretKey) getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    //resolveToken
    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            String token = bearer.substring(7).trim(); // trim() 추가
            return token.isEmpty() ? null : token;
        }
        return null;
    }

    //토큰 전처리 메서드
    public String preprocessToken(String token) {
        if (token == null) {
            throw new IllegalArgumentException("JWT token cannot be null");
        }

        // 공백 제거
        token = token.trim();

        // Bearer 접두사가 있다면 제거 (방어적 코딩)
        if (token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        // 빈 문자열 검증
        if (token.isEmpty()) {
            throw new IllegalArgumentException("JWT token is empty after preprocessing");
        }

        return token;
    }
}
