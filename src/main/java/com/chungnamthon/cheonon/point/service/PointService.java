package com.chungnamthon.cheonon.point.service;

import com.chungnamthon.cheonon.auth.jwt.JwtUtil;
import com.chungnamthon.cheonon.global.exception.BusinessException;
import com.chungnamthon.cheonon.global.exception.error.AuthenticationError;
import com.chungnamthon.cheonon.point.domain.Point;
import com.chungnamthon.cheonon.point.domain.value.PaymentType;
import com.chungnamthon.cheonon.point.dto.response.PointHistoryResponse;
import com.chungnamthon.cheonon.point.repository.PointRepository;
import com.chungnamthon.cheonon.user.domain.User;
import com.chungnamthon.cheonon.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    public static final int POINT_SIGN_UP = 1000;
    public static final int POINT_MEETING_PARTICIPATION = 50;
    public static final int POINT_MEETING_CREATION = 70;
    public static final int POINT_CHEONAN_CARD_VERIFICATION = 100;
    public static final int POINT_AFFILIATE_BONUS = 200;

    private final PointRepository pointRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public List<PointHistoryResponse> getMyPointHistory(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        List<Point> pointHistories = pointRepository.findByUserIdOrderByCreatedAtDesc(userId);

        List<PointHistoryResponse> myPointHistory = new ArrayList<>();
        for (Point point : pointHistories) {
            myPointHistory.add(
                    new PointHistoryResponse(
                            point.getId(),
                            point.getPaymentType(),
                            point.getChangedPoint(),
                            point.getCreatedAt()
                    )
            );
        }
        return myPointHistory;
    }

    public void rewardForSignUp(Long userId) {
        if (pointRepository.existsByUserIdAndPaymentTypeAndCreatedAtBetween(
                userId, PaymentType.SIGN_UP,
                LocalDateTime.now().minusYears(10), LocalDateTime.now())) {
            return;
        }
        reward(userId, POINT_SIGN_UP, PaymentType.SIGN_UP);
    }

    public void rewardForMeetingParticipation(Long userId) {
        if (isOverWeeklyLimit(userId, PaymentType.MEETING_PARTICIPATION, 3)) return;
        reward(userId, POINT_MEETING_PARTICIPATION, PaymentType.MEETING_PARTICIPATION);
    }

    public void rewardForMeetingCreation(Long userId) {
        if (isOverWeeklyLimit(userId, PaymentType.MEETING_CREATION, 1)) return;
        reward(userId, POINT_MEETING_CREATION, PaymentType.MEETING_CREATION);
    }

    public void rewardForReceiptVerification(Long userId, boolean isAffiliate) {
        PaymentType type = isAffiliate ? PaymentType.PARTNER_STORE_BONUS : PaymentType.PAYMENT_VERIFICATION;
        int pointAmount = isAffiliate ? POINT_AFFILIATE_BONUS : POINT_CHEONAN_CARD_VERIFICATION;

        if (isOverWeeklyLimit(userId, type, 5)) return;
        reward(userId, pointAmount, type);
    }

    public Long getCurrentPoint(Long userId) {
        return pointRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .map(p -> (long) p.getCurrentPoint())
                .orElse(0L);
    }

    private boolean isOverWeeklyLimit(Long userId, PaymentType type, int limit) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monday = now.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
        return pointRepository.findByUserIdAndPaymentTypeAndCreatedAtBetween(userId, type, monday, now).size() >= limit;
    }

    private void reward(Long userId, int changedPoint, PaymentType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(AuthenticationError.USER_NOT_FOUND));

        int previousPoint = pointRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .map(Point::getCurrentPoint)
                .orElse(0);

        int updatedPoint = previousPoint + changedPoint;

        Point point = Point.builder()
                .user(user)
                .paymentType(type)
                .changedPoint(changedPoint)
                .currentPoint(updatedPoint)
                .build();

        pointRepository.save(point);
    }
}