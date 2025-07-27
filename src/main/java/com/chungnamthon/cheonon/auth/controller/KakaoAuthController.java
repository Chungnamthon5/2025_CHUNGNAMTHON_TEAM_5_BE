package com.chungnamthon.cheonon.auth.controller;

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
    public ResponseEntity<Void> kakaoLogin(@RequestParam("code") String code) {
        try {
            TokenResponse tokenResponse = kakaoOauthService.kakaoLogin(code);

            String redirectUrl = UriComponentsBuilder
                    .fromHttpUrl("https://2025-chungnamthon-team-5-fe.vercel.app/callback")
                    .queryParam("accessToken", tokenResponse.getAccessToken())
                    .queryParam("refreshToken", tokenResponse.getRefreshToken())
                    .build()
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(redirectUrl));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);

        } catch (Exception e) {
            // 프론트에 실패 알림용 fallback URL로 리디렉션
            String fallbackUrl = "https://2025-chungnamthon-team-5-fe.vercel.app/error?msg=login_failed";
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(fallbackUrl));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
    }
}