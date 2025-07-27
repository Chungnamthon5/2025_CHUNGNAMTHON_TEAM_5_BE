package com.chungnamthon.cheonon.poweruser.service;

import com.chungnamthon.cheonon.poweruser.domain.PowerUser;
import com.chungnamthon.cheonon.poweruser.dto.PowerUserResponse;
import com.chungnamthon.cheonon.point.repository.PointRepository;
import com.chungnamthon.cheonon.poweruser.PowerUserRepository;
import com.chungnamthon.cheonon.user.domain.User;
import com.chungnamthon.cheonon.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PowerUserService {

    private final PointRepository pointRepository;
    private final PowerUserRepository powerUserRepository;
    private final UserRepository userRepository;

    @Transactional
    public void updateWeeklyTopUsers() {
        LocalDate thisWeekMonday = LocalDate.now().with(DayOfWeek.MONDAY); // 기준 주

        List<Object[]> topUsers = pointRepository.findTopUsersByTotalPoint(PageRequest.of(0, 5));
        Set<Long> seenUserIds = new HashSet<>();
        int rank = 1;

        for (Object[] row : topUsers) {
            Long userId = (Long) row[0];
            if (seenUserIds.contains(userId)) continue;

            Integer totalPoint = ((Number) row[1]).intValue();
            User user = userRepository.findById(userId).orElseThrow();

            PowerUser powerUser = new PowerUser(user, totalPoint, rank++, thisWeekMonday); // ← ✅ 여기에 주차 정보 포함
            powerUserRepository.save(powerUser);

            seenUserIds.add(userId);
        }
    }

    @Transactional(readOnly = true)
    public List<PowerUserResponse> getRecentPowerUsers() {
        LocalDate thisWeek = LocalDate.now().with(DayOfWeek.MONDAY); // 이번 주 월요일
        return powerUserRepository.findTop5ByWeekOfOrderByRankAsc(thisWeek)
                .stream()
                .map(PowerUserResponse::from)
                .toList();
    }
}