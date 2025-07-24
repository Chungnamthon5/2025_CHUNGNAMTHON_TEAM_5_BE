package com.chungnamthon.cheonon.point.dto.response;

import com.chungnamthon.cheonon.point.domain.value.PaymentType;

import java.time.LocalDateTime;

public record PointHistoryResponse(
        Long pointId,
        PaymentType paymentType,
        Integer changedPoint,
        LocalDateTime usedAt
) {
}
