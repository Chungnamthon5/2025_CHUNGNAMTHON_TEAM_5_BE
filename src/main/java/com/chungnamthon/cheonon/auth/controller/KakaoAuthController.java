package com.chungnamthon.cheonon.auth.controller;

import com.chungnamthon.cheonon.auth.dto.response.ApiResponse;
import com.chungnamthon.cheonon.auth.dto.response.TokenResponse;
import com.chungnamthon.cheonon.auth.service.KakaoOauthService;
import com.chungnamthon.cheonon.global.payload.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/kakao")
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoOauthService kakaoOauthService;

    @GetMapping("/callback")
    public ResponseDto<ApiResponse<TokenResponse>> kakaoLogin(@RequestParam("code") String code) {
        TokenResponse tokenResponse = kakaoOauthService.kakaoLogin(code);
        return ResponseDto.of(ApiResponse.success("카카오 로그인 성공", tokenResponse));
    }
}