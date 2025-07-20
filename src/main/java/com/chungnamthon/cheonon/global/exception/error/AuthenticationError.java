package com.chungnamthon.cheonon.global.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthenticationError implements BaseError {

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 리프레시 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    KAKAO_CODE_INVALID(HttpStatus.BAD_REQUEST, "카카오 인가 코드가 유효하지 않습니다."),
    KAKAO_USER_FETCH_FAIL(HttpStatus.UNAUTHORIZED, "카카오 사용자 정보를 불러오는 데 실패했습니다."),
    KAKAO_EMAIL_NOT_PROVIDED(HttpStatus.BAD_REQUEST, "이메일 제공에 동의하지 않았습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}