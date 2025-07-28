package com.chungnamthon.cheonon.global.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PowerUserError implements BaseError {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "파워 유저에 해당하는 유저를 찾을 수 없습니다."),
    NO_POWER_USER_THIS_WEEK(HttpStatus.NOT_FOUND, "이번 주 파워 유저가 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}