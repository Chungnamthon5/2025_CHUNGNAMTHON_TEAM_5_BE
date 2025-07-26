package com.chungnamthon.cheonon.global.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonError implements BaseError {

    INVALID_PARAMETER(HttpStatus.NOT_FOUND, "유효하지 않은 파라미터입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
