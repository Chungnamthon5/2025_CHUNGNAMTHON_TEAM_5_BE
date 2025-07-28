package com.chungnamthon.cheonon.poweruser.service;

import com.chungnamthon.cheonon.global.exception.BusinessException;
import com.chungnamthon.cheonon.global.exception.error.PowerUserError;
import com.chungnamthon.cheonon.point.repository.PointRepository;
import com.chungnamthon.cheonon.poweruser.PowerUserRepository;
import com.chungnamthon.cheonon.poweruser.domain.PowerUser;
import com.chungnamthon.cheonon.poweruser.dto.PowerUserResponse;
import com.chungnamthon.cheonon.user.domain.User;
import com.chungnamthon.cheonon.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PowerUserService {

    private final PointRepository pointRepository;
    private final PowerUserRepository powerUserRepository;
    private final UserRepository userRepository;

    /**
     * 매주 월요일마다 파워유저 선정 및 저장
     */
    @Transactional
    public void updateWeeklyTopUsers() {
        LocalDate thisWeekMonday = LocalDate.now().with(DayOfWeek.MONDAY);

        List<Object[]> topUsers = pointRepository.findTopUsersByTotalPoint(PageRequest.of(0, 5));
        Set<Long> seenUserIds = new HashSet<>();
        int rank = 1;

        for (Object[] row : topUsers) {
            try {
                Long userId = (Long) row[0];
                if (!seenUserIds.add(userId)) continue;

                Integer totalPoint = ((Number) row[1]).intValue();

                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new BusinessException(PowerUserError.USER_NOT_FOUND));

                PowerUser powerUser = new PowerUser(user, totalPoint, rank++, thisWeekMonday);
                powerUserRepository.save(powerUser);

            } catch (BusinessException be) {
                log.warn("파워 유저 저장 실패 (도메인 예외): {}", be.getMessage());
            } catch (Exception e) {
                log.error("파워 유저 저장 중 예기치 못한 오류: {}", row[0], e);
            }
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
}