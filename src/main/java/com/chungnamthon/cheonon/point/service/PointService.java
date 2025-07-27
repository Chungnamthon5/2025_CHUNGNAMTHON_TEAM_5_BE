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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    public static final int POINT_MEETING_PARTICIPATION = 10;
    public static final int POINT_MEETING_CREATION = 20;
    public static final int POINT_CHEONAN_CARD_VERIFICATION = 50;
    public static final int POINT_AFFILIATE_BONUS = 20;
    public static final int POINT_WEEKLY_STREAK_BONUS = 30;

    private final PointRepository pointRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public List<PointHistoryResponse> getMyPointHistory(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        List<Point> pointHistories = pointRepository.findByUserIdOrderByCreatedAtDesc(userId);

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

    /**
     * 모임 1회 참여 보상 포인트 지급
     * @param userId
     */
    public void rewardForMeetingParticipation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(AuthenticationError.USER_NOT_FOUND));

        Point latestPoint = pointRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElse(null);

        int previousPoint = 0;
        if (latestPoint != null) {
            previousPoint = latestPoint.getCurrentPoint();
        }

        int updatedPoint = previousPoint + POINT_MEETING_PARTICIPATION;

        Point point = Point.builder()
                .user(user)
                .paymentType(PaymentType.MEETING_PARTICIPATION)
                .changedPoint(POINT_MEETING_PARTICIPATION)
                .currentPoint(updatedPoint)
                .build();

        pointRepository.save(point);
    }

    /**
     * 모임 개설 보상 포인트 지급
     * @param userId
     */
    public void rewardForMeetingCreation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(AuthenticationError.USER_NOT_FOUND));

        Point latestPoint = pointRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElse(null);

        int previousPoint = 0;
        if (latestPoint != null) {
            previousPoint = latestPoint.getCurrentPoint();
        }

        int updatedPoint = previousPoint + POINT_MEETING_CREATION;

        Point point = Point.builder()
                .user(user)
                .paymentType(PaymentType.MEETING_CREATION)
                .changedPoint(POINT_MEETING_CREATION)
                .currentPoint(updatedPoint)
                .build();

        pointRepository.save(point);
    }

    /**
     * 천안사랑카드 인증 보상 포인트 지급
     * @param userId
     */
    public Point rewardForCheonanCardVerification(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(AuthenticationError.USER_NOT_FOUND));

        Point latestPoint = pointRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElse(null);

        int previousPoint = 0;
        if (latestPoint != null) {
            previousPoint = latestPoint.getCurrentPoint();
        }

        int updatedPoint = previousPoint + POINT_CHEONAN_CARD_VERIFICATION;

        Point point = Point.builder()
                .user(user)
                .paymentType(PaymentType.PAYMENT_VERIFICATION)
                .changedPoint(POINT_CHEONAN_CARD_VERIFICATION)
                .currentPoint(updatedPoint)
                .build();

        pointRepository.save(point);
        return point;
    }

    /**
     * 제휴 업체 인증 보상 포인트 지급
     * @param userId
     */
    public void rewardForAffiliateStoreProof(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(AuthenticationError.USER_NOT_FOUND));

        Point latestPoint = pointRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElse(null);

        int previousPoint = 0;
        if (latestPoint != null) {
            previousPoint = latestPoint.getCurrentPoint();
        }

        int updatedPoint = previousPoint + POINT_AFFILIATE_BONUS;

        Point point = Point.builder()
                .user(user)
                .paymentType(PaymentType.PARTNER_STORE_BONUS)
                .changedPoint(POINT_AFFILIATE_BONUS)
                .currentPoint(updatedPoint)
                .build();

        pointRepository.save(point);
    }

    public void rewardForWeeklyStreakParticipation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(AuthenticationError.USER_NOT_FOUND));

        Point latestPoint = pointRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElse(null);

        int previousPoint = 0;
        if (latestPoint != null) {
            previousPoint = latestPoint.getCurrentPoint();
        }

        int updatedPoint = previousPoint + POINT_WEEKLY_STREAK_BONUS;

        Point point = Point.builder()
                .user(user)
                .paymentType(PaymentType.WEEKLY_STREAK_BONUS)
                .changedPoint(POINT_WEEKLY_STREAK_BONUS)
                .currentPoint(updatedPoint)
                .build();

        pointRepository.save(point);
    }
}
