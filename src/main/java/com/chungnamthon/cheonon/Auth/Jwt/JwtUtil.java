package com.chungnamthon.cheonon.Auth.Jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKeyString;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    // Base64 인코딩된 키 → Key 객체로 변환
    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyString);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Access Token 생성
    public String createAccessToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 리프레시 토큰의 만료일을 LocalDateTime으로 반환 (DB 저장용)
    // Access Token + 만료일 리턴
    public TokenWithExpiry createAccessTokenWithExpiry(Long userId) {
        long nowMillis = System.currentTimeMillis();
        Date expiryDate = new Date(nowMillis + accessExpiration);

        String token = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date(nowMillis))
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        return new TokenWithExpiry(token, expiryDate.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime());
    }

    // Refresh Token + 만료일 리턴
    public TokenWithExpiry createRefreshTokenWithExpiry(Long userId) {
        long nowMillis = System.currentTimeMillis();
        Date expiryDate = new Date(nowMillis + refreshExpiration);

        String token = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date(nowMillis))
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        return new TokenWithExpiry(token, expiryDate.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime());
    }

    // 토큰에서 사용자 ID 추출
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // HTTP 요청에서 토큰 추출
    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return (bearer != null && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
    }
}