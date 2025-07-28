package com.chungnamthon.cheonon.poweruser.scheduler;

import com.chungnamthon.cheonon.poweruser.service.PowerUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PowerUserScheduler {

    private final PowerUserService powerUserService;

    /**
     * 매주 월요일 새벽 3시 파워유저 갱신
     */
    @Scheduled(cron = "0 0 3 * * MON", zone = "Asia/Seoul")
    public void update() {
        log.info("파워유저 스케줄 시작");
        powerUserService.updateWeeklyTopUsers();
        log.info("파워유저 스케줄 종료");
    }
}