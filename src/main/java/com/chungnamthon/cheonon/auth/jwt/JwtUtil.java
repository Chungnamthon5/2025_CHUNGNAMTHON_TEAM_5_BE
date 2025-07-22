package com.chungnamthon.cheonon.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
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

    public Long getUserIdFromToken(String token) {
        Jws<Claims> jwt = Jwts.parser()  // ✅ parserBuilder() → parser()
                .verifyWith((SecretKey) getSigningKey())     // ✅ verifyWith로 서명 검증 설정
                .build()
                .parseSignedClaims(token);       // ✅ parseClaimsJws → parseSignedClaims

        return Long.parseLong(jwt.getPayload().getSubject());  // ✅ getBody() → getPayload()
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return (bearer != null && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
    }
}