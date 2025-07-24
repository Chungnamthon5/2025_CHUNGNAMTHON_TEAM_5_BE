package com.chungnamthon.cheonon.coupon.dto.response;

import java.time.LocalDate;

public record MyCouponListResponse(
        Long couponId,
        Long userId,
        String title,
        Integer point,
        LocalDate expirationPeriod,
        String imageUrl
) {
}
