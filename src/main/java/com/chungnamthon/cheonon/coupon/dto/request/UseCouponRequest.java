package com.chungnamthon.cheonon.coupon.dto.request;

public record UseCouponRequest(
        Long couponId,
        Long confirmCode
) {
}
