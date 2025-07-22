package com.chungnamthon.cheonon.auth.controller;

import com.chungnamthon.cheonon.auth.dto.request.RefreshTokenRequest;
import com.chungnamthon.cheonon.auth.dto.response.TokenResponse;
import com.chungnamthon.cheonon.auth.service.AuthService;
import com.chungnamthon.cheonon.global.payload.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class TokenController {

    private final AuthService authService;

    @PostMapping("/refresh")
    public ResponseDto<TokenResponse> refresh(@RequestBody RefreshTokenRequest requestDto) {
        TokenResponse response = authService.refresh(requestDto.getRefreshToken());
        return ResponseDto.of(response, "Refresh token has been issued successfully.");
    }

    @PostMapping("/logout")
    public ResponseDto<String> logout(@RequestBody RefreshTokenRequest requestDto) {
        authService.logout(requestDto.getRefreshToken());
        return ResponseDto.of("Successfully logged out");
    }
}