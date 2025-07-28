package com.chungnamthon.cheonon.auth.service;

import com.chungnamthon.cheonon.auth.domain.RefreshToken;
import com.chungnamthon.cheonon.auth.dto.response.KakaoTokenResponse;
import com.chungnamthon.cheonon.auth.dto.response.KakaoUserResponse;
import com.chungnamthon.cheonon.auth.dto.response.TokenResponse;
import com.chungnamthon.cheonon.auth.jwt.JwtUtil;
import com.chungnamthon.cheonon.auth.jwt.TokenWithExpiry;
import com.chungnamthon.cheonon.auth.repository.RefreshTokenRepository;
import com.chungnamthon.cheonon.global.exception.BusinessException;
import com.chungnamthon.cheonon.global.exception.error.AuthenticationError;
import com.chungnamthon.cheonon.point.service.PointService;
import com.chungnamthon.cheonon.user.domain.User;
import com.chungnamthon.cheonon.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoOauthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PointService pointService;

    @Getter
    @Value("${kakao.rest-api-key}")
    private String clientId;

    @Getter
    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getAccessToken(String code) {
        String tokenUri = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        params.add("client_secret", clientSecret);

        try {
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            ResponseEntity<KakaoTokenResponse> response = restTemplate.postForEntity(tokenUri, request, KakaoTokenResponse.class);

            KakaoTokenResponse body = response.getBody();
            if (body == null || body.getAccess_token() == null) {
                log.warn("카카오 토큰 응답 body가 null이거나 access_token이 없음: {}", response);
                throw new BusinessException(AuthenticationError.KAKAO_CODE_INVALID);
            }

            return body.getAccess_token();
        } catch (RestClientException e) {
            log.warn("카카오 토큰 요청 실패", e);
            throw new BusinessException(AuthenticationError.KAKAO_CODE_INVALID);
        }
    }

    public KakaoUserResponse getKakaoUser(String accessToken) {
        String userInfoUri = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            //원본 JSON 문자열 로그 찍기
            ResponseEntity<String> rawResponse = restTemplate.exchange(userInfoUri, HttpMethod.GET, request, String.class);
            System.out.println("Kakao User Raw Response: " + rawResponse.getBody());

            //DTO로 파싱 시도
            ResponseEntity<KakaoUserResponse> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, request, KakaoUserResponse.class);
            KakaoUserResponse body = response.getBody();

            if (body == null) {
                log.warn("카카오 사용자 응답 body가 null임");
                throw new BusinessException(AuthenticationError.KAKAO_USER_FETCH_FAIL);
            }

            return body;
        } catch (HttpStatusCodeException e) {
            System.out.println("Kakao API Error Response: " + e.getResponseBodyAsString());
            throw new BusinessException(AuthenticationError.KAKAO_USER_FETCH_FAIL);
        } catch (RestClientException e) {
            throw new BusinessException(AuthenticationError.KAKAO_USER_FETCH_FAIL);
        }
    }

    @Transactional
    public TokenResponse kakaoLogin(String code) {
        String kakaoAccessToken = getAccessToken(code);
        KakaoUserResponse kakaoUser = getKakaoUser(kakaoAccessToken);

        if (kakaoUser.getKakao_account() == null) {
            throw new BusinessException(AuthenticationError.KAKAO_ACCOUNT_NOT_PROVIDED);
        }

        String email = kakaoUser.getKakao_account().getEmail();
        if (email == null || email.isBlank()) {
            throw new BusinessException(AuthenticationError.KAKAO_EMAIL_NOT_PROVIDED);
        }

        // 최초 가입 여부 체크 및 포인트 지급
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = userRepository.save(User.builder()
                            .email(email)
                            .nickname(kakaoUser.getKakao_account().getProfile().getNickname())
                            .image(kakaoUser.getKakao_account().getProfile().getThumbnail_image_url())
                            .role("USER")
                            .build());

                    pointService.rewardForSignUp(newUser.getId());

                    return newUser;
                });

        // 기존 리프레시 토큰 삭제
        refreshTokenRepository.deleteAllByUser_Id(user.getId());

        // 새 토큰 발급 및 저장
        String jwtAccessToken = jwtUtil.createAccessToken(user.getId());
        TokenWithExpiry refreshTokenWithExpiry = jwtUtil.createRefreshTokenWithExpiry(user.getId());

        RefreshToken newToken = RefreshToken.builder()
                .token(refreshTokenWithExpiry.getToken())
                .createdAt(LocalDateTime.now())
                .expiredAt(refreshTokenWithExpiry.getExpiry())
                .user(user)
                .build();

        refreshTokenRepository.save(newToken);

        return new TokenResponse(jwtAccessToken, refreshTokenWithExpiry.getToken());
    }

}