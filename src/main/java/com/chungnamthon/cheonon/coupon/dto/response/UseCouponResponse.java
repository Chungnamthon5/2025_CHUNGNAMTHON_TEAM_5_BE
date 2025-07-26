package com.chungnamthon.cheonon.coupon.dto.response;

import java.time.LocalDateTime;

public record UseCouponResponse(
        Long couponId,
        String title,
        LocalDateTime usedAt
) {
}
