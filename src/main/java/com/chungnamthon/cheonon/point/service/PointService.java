package com.chungnamthon.cheonon.point.service;

import com.chungnamthon.cheonon.auth.jwt.JwtUtil;
import com.chungnamthon.cheonon.point.domain.Point;
import com.chungnamthon.cheonon.point.domain.value.PaymentType;
import com.chungnamthon.cheonon.point.dto.response.PointHistoryResponse;
import com.chungnamthon.cheonon.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final JwtUtil jwtUtil;

    public List<PointHistoryResponse> getMyPointHistory(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        List<Point> pointHistories = pointRepository.findByUserId(userId);

        List<PointHistoryResponse> myPointHistory = new ArrayList<>();
        for (Point point : pointHistories) {
            Long pointHistoryId = point.getId();
            PaymentType paymentType = point.getPaymentType();
            Integer changedPoint = point.getChangedPoint();
            LocalDateTime usedAt = point.getCreatedAt();

            PointHistoryResponse pointHistoryResponse
                    = new PointHistoryResponse(pointHistoryId, paymentType, changedPoint, usedAt);

            myPointHistory.add(pointHistoryResponse);
        }

        return myPointHistory;
    }
}
