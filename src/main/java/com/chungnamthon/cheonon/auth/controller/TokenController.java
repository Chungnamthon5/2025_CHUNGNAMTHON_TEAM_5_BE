package com.chungnamthon.cheonon.auth.controller;

import com.chungnamthon.cheonon.auth.dto.response.ApiResponse;
import com.chungnamthon.cheonon.auth.dto.request.RefreshTokenRequest;
import com.chungnamthon.cheonon.auth.dto.response.TokenResponse;
import com.chungnamthon.cheonon.auth.service.AuthService;
import com.chungnamthon.cheonon.global.payload.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class TokenController {

    private final AuthService authService;

    @PostMapping("/refresh")
    public ResponseDto<ApiResponse<TokenResponse>> refresh(@RequestBody RefreshTokenRequest requestDto) {
        return ResponseDto.of(authService.refresh(requestDto.getRefreshToken()), "Refresh token has been issued successfully.");
    }

    @PostMapping("/logout")
    public ResponseDto<ApiResponse<String>> logout(@RequestBody RefreshTokenRequest requestDto) {
        authService.logout(requestDto.getRefreshToken());
        return ResponseDto.of("Successfully logged out");
    }
}