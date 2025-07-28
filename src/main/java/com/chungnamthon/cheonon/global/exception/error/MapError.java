package com.chungnamthon.cheonon.global.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MapError implements BaseError {

    MERCHANT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 가맹점을 찾을 수 없습니다."),
    AFFILIATE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 제휴업체를 찾을 수 없습니다.");


    private final HttpStatus httpStatus;
    private final String message;
}
