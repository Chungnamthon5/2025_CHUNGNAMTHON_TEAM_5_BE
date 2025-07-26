package com.chungnamthon.cheonon.global.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PointError implements BaseError {

    POINT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 포인트입니다."),
    INSUFFICIENT_POINT(HttpStatus.BAD_REQUEST, "포인트가 부족합니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
