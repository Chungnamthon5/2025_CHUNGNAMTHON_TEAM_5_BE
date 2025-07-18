package com.chungnamthon.cheonon.Auth.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken; // 필요 시 null일 수 있음
}