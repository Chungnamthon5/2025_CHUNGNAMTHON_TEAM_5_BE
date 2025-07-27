package com.chungnamthon.cheonon.global.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CouponError implements BaseError {

    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 쿠폰입니다."),
    COUPON_ALREADY_ISSUED(HttpStatus.BAD_REQUEST, "이미 발급받은 쿠폰입니다."),
    COUPON_ALREADY_USED(HttpStatus.BAD_REQUEST, "이미 사용한 쿠폰입니다."),
    COUPON_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "확인 코드가 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
