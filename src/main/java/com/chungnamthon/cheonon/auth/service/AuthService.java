package com.chungnamthon.cheonon.auth.service;

import com.chungnamthon.cheonon.auth.domain.RefreshToken;
import com.chungnamthon.cheonon.auth.dto.response.TokenResponse;
import com.chungnamthon.cheonon.auth.jwt.JwtUtil;
import com.chungnamthon.cheonon.auth.repository.RefreshTokenRepository;
import com.chungnamthon.cheonon.global.exception.BusinessException;
import com.chungnamthon.cheonon.global.exception.error.AuthenticationError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenResponse refresh(String refreshToken) {
        RefreshToken tokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(AuthenticationError.INVALID_REFRESH_TOKEN));

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BusinessException(AuthenticationError.REFRESH_TOKEN_EXPIRED);
        }

        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        String newAccessToken = jwtUtil.createAccessToken(userId);

        return new TokenResponse(newAccessToken, null); // 리프레시 토큰은 재발급 안 하는 정책이라면 null
    }

    public void logout(String refreshToken) {
        RefreshToken tokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> {
                    log.warn("로그아웃 요청에서 토큰이 DB에 존재하지 않음: {}", refreshToken);
                    return new BusinessException(AuthenticationError.INVALID_REFRESH_TOKEN);
                });

        refreshTokenRepository.delete(tokenEntity);
    }
}