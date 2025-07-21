package com.chungnamthon.cheonon.auth.controller;

import com.chungnamthon.cheonon.auth.dto.response.ApiResponse;
import com.chungnamthon.cheonon.auth.dto.response.TokenResponse;
import com.chungnamthon.cheonon.auth.service.KakaoOauthService;
import com.chungnamthon.cheonon.global.payload.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/auth/kakao")
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoOauthService kakaoOauthService;

    // ✅ 1단계: 프론트가 호출할 최초 엔드포인트 (카카오 로그인 페이지로 리디렉트)
    @GetMapping("")
    public ResponseEntity<Void> redirectToKakao() {
        String kakaoAuthorizeUrl = UriComponentsBuilder.fromHttpUrl("https://kauth.kakao.com/oauth/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", kakaoOauthService.getClientId())
                .queryParam("redirect_uri", kakaoOauthService.getRedirectUri())
                .build()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(kakaoAuthorizeUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    // ✅ 2단계: 카카오에서 인가코드 받아오는 콜백 엔드포인트
    @GetMapping("/callback")
    public ResponseDto<ApiResponse<TokenResponse>> kakaoLogin(@RequestParam("code") String code) {
        TokenResponse tokenResponse = kakaoOauthService.kakaoLogin(code);
        return ResponseDto.of(ApiResponse.success("카카오 로그인 성공", tokenResponse));
    }
}