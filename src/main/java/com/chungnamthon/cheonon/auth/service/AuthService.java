package com.chungnamthon.cheonon.auth.service;

import com.chungnamthon.cheonon.auth.domain.RefreshToken;
import com.chungnamthon.cheonon.auth.dto.response.TokenResponse;
import com.chungnamthon.cheonon.auth.jwt.JwtUtil;
import com.chungnamthon.cheonon.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenResponse refresh(String refreshToken) {
        RefreshToken tokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다."));

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("만료된 리프레시 토큰입니다.");
        }

        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        String newAccessToken = jwtUtil.createAccessToken(userId);

        return new TokenResponse(newAccessToken, null); // 리프레시 토큰은 재발급 안 하는 정책이라면 null
    }

    public void logout(String refreshToken) {
        RefreshToken tokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("해당 리프레시 토큰이 존재하지 않습니다."));

        refreshTokenRepository.delete(tokenEntity);
    }
}