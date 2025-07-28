package com.chungnamthon.cheonon.poweruser.service;

import com.chungnamthon.cheonon.global.exception.BusinessException;
import com.chungnamthon.cheonon.global.exception.error.PowerUserError;
import com.chungnamthon.cheonon.meeting.repository.MeetingUserRepository;
import com.chungnamthon.cheonon.point.repository.PointRepository;
import com.chungnamthon.cheonon.poweruser.PowerUserRepository;
import com.chungnamthon.cheonon.poweruser.domain.PowerUser;
import com.chungnamthon.cheonon.poweruser.dto.PowerUserResponse;
import com.chungnamthon.cheonon.receipt_ocr.repository.ReceiptRepository;
import com.chungnamthon.cheonon.user.domain.User;
import com.chungnamthon.cheonon.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PowerUserService {

    private final PointRepository pointRepository;
    private final PowerUserRepository powerUserRepository;
    private final UserRepository userRepository;
    private final ReceiptRepository receiptRepository;
    private final MeetingUserRepository meetingUserRepository;

    /**
     * 매주 월요일마다 파워유저 선정 및 저장
     */
    @Transactional
    public void updateWeeklyTopUsers() {
        LocalDate thisWeekMonday = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDateTime weekStart = thisWeekMonday.atStartOfDay();
        LocalDateTime weekEnd = thisWeekMonday.plusDays(6).atTime(LocalTime.MAX);

        List<User> users = userRepository.findAll();
        List<UserScore> scores = new ArrayList<>();

        for (User user : users) {
            Long userId = user.getId();

            int receiptCount = receiptRepository.countReceiptByUserIdAndPeriod(userId, weekStart, weekEnd);
            int meetingCount = meetingUserRepository.countMeetingParticipationByUserIdAndPeriod(userId, weekStart, weekEnd);
            int thisWeekPoint = pointRepository.sumPointByUserIdAndPeriod(userId, weekStart, weekEnd);
            int totalPoint = pointRepository.sumPointByUserId(userId); // 누적 포인트

            // 기본 점수 계산
            double baseScore = (receiptCount * 10.0) + (meetingCount * 10.0) + (thisWeekPoint * 0.6);

            // 최종 점수 계산
            double finalScore = (baseScore * 0.8) + (totalPoint * 0.2);

            scores.add(new UserScore(user, finalScore));
        }

        scores.sort(Comparator.comparingDouble(UserScore::score).reversed());

        int rank = 1;
        for (UserScore userScore : scores.subList(0, Math.min(scores.size(), 5))) {
            PowerUser powerUser = PowerUser.builder()
                    .user(userScore.user())
                    .totalPoint((int) userScore.score())
                    .ranking(rank++)
                    .weekOf(thisWeekMonday)
                    .build();
            powerUserRepository.save(powerUser);
        }
    }

    /**
     * 이번 주 파워유저 조회
     */
    @Transactional(readOnly = true)
    public List<PowerUserResponse> getRecentPowerUsers() {
        LocalDate thisWeek = LocalDate.now().with(DayOfWeek.MONDAY);

        List<PowerUser> powerUsers = powerUserRepository.findTop5ByWeekOfOrderByRankingAsc(thisWeek);

        if (powerUsers.isEmpty()) {
            throw new BusinessException(PowerUserError.NO_POWER_USER_THIS_WEEK);
        }

        return powerUsers.stream()
                .map(PowerUserResponse::from)
                .toList();
    }

    private record UserScore(User user, double score) {}
}