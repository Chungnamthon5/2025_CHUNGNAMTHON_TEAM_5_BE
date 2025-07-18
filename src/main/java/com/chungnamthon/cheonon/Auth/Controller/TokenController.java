package com.chungnamthon.cheonon.Auth.Controller;

import com.chungnamthon.cheonon.Auth.Dto.ApiResponse;
import com.chungnamthon.cheonon.Auth.Dto.RefreshTokenRequestDto;
import com.chungnamthon.cheonon.Auth.Dto.TokenResponseDto;
import com.chungnamthon.cheonon.Auth.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<TokenResponseDto>> refresh(@RequestBody RefreshTokenRequestDto requestDto) {
        return ResponseEntity.ok(authService.refresh(requestDto.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestBody RefreshTokenRequestDto requestDto) {
        authService.logout(requestDto.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("로그아웃 성공", null));
    }
}