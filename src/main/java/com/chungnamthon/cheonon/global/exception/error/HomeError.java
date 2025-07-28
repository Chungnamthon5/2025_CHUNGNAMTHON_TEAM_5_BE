package com.chungnamthon.cheonon.global.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum HomeError implements BaseError {

    POWER_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "파워 유저 데이터가 존재하지 않습니다."),
    AFFILIATE_NOT_FOUND(HttpStatus.NOT_FOUND, "제휴 업체 데이터가 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}