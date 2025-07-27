package com.chungnamthon.cheonon.auth.controller;

import com.chungnamthon.cheonon.auth.dto.response.TokenResponse;
import com.chungnamthon.cheonon.auth.service.KakaoOauthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<Void> kakaoLogin(@RequestParam("code") String code, HttpServletRequest request) {
        try {
            TokenResponse tokenResponse = kakaoOauthService.kakaoLogin(code);

            // 요청 출처에 따라 콜백 URL 결정
            String callbackUrl = determineCallbackUrl(request);

            String redirectUrl = UriComponentsBuilder
                    .fromHttpUrl(callbackUrl)
                    .queryParam("accessToken", tokenResponse.getAccessToken())
                    .queryParam("refreshToken", tokenResponse.getRefreshToken())
                    .build()
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(redirectUrl));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);

        } catch (Exception e) {
            // 프론트에 실패 알림용 fallback URL로 리디렉션
            String fallbackUrl = determineFallbackUrl(request);
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(fallbackUrl));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
    }

    /**
     * 요청 출처에 따라 콜백 URL을 결정하는 메서드
     */
    private String determineCallbackUrl(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        String origin = request.getHeader("Origin");

        // Origin 헤더 우선 확인
        if (origin != null && origin.startsWith("http://localhost")) {
            return "http://localhost:5173/callback";
        }

        // Referer 헤더 확인
        if (referer != null && referer.startsWith("http://localhost")) {
            return "http://localhost:5173/callback";
        }

        // 기본값은 배포 환경
        return "https://2025-chungnamthon-team-5-fe.vercel.app/callback";
    }

    /**
     * 에러 발생 시 fallback URL을 결정하는 메서드
     */
    private String determineFallbackUrl(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        String origin = request.getHeader("Origin");

        // Origin 헤더 우선 확인
        if (origin != null && origin.startsWith("http://localhost")) {
            return "http://localhost:5173/error?msg=login_failed";
        }

        // Referer 헤더 확인
        if (referer != null && referer.startsWith("http://localhost")) {
            return "http://localhost:5173/error?msg=login_failed";
        }

        // 기본값은 배포 환경
        return "https://2025-chungnamthon-team-5-fe.vercel.app/error?msg=login_failed";
    }
}