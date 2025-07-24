package com.chungnamthon.cheonon.coupon.dto.response;

public record CouponListResponse(
        Long couponId,
        String title,
        Integer point,
        Integer expirationPeriod,
        String image
) {
}
