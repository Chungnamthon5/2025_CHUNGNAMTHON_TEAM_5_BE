package com.chungnamthon.cheonon.Auth.Service;

import com.chungnamthon.cheonon.Auth.Dto.ApiResponse;
import com.chungnamthon.cheonon.Auth.Dto.TokenResponseDto;
import com.chungnamthon.cheonon.Auth.Entity.RefreshTokenEntity;
import com.chungnamthon.cheonon.Auth.Jwt.JwtUtil;
import com.chungnamthon.cheonon.Auth.Repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public ApiResponse<TokenResponseDto> refresh(String refreshToken) {
        RefreshTokenEntity tokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다."));

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("만료된 리프레시 토큰입니다.");
        }

        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        String newAccessToken = jwtUtil.createAccessToken(userId);

        TokenResponseDto tokenResponseDto = new TokenResponseDto(newAccessToken, null);
        return ApiResponse.success("토큰 재발급 성공", tokenResponseDto);
    }

    public void logout(String refreshToken) {
        RefreshTokenEntity tokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("해당 리프레시 토큰이 존재하지 않습니다."));

        refreshTokenRepository.delete(tokenEntity);
    }
}