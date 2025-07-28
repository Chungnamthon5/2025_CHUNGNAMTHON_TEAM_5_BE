package com.chungnamthon.cheonon.home.service;

import com.chungnamthon.cheonon.global.exception.BusinessException;
import com.chungnamthon.cheonon.global.exception.error.HomeError;
import com.chungnamthon.cheonon.home.dto.HomeResponse;
import com.chungnamthon.cheonon.map.dto.AffiliateHomePreviewResponse;
import com.chungnamthon.cheonon.map.service.AffiliateService;
import com.chungnamthon.cheonon.meeting.dto.response.MeetingPreviewResponse;
import com.chungnamthon.cheonon.meeting.repository.MeetingRepository;
import com.chungnamthon.cheonon.poweruser.dto.PowerUserResponse;
import com.chungnamthon.cheonon.poweruser.service.PowerUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeService {

    private final MeetingRepository meetingRepository;
    private final AffiliateService affiliateService;
    private final PowerUserService powerUserService;

    public HomeResponse getHomeData() {
        return HomeResponse.builder()
                .recentMeetings(getRecentMeetings())
                .topAffiliates(getAffiliates())
                .powerUsers(getPowerUsersOrThrow())
                .build();
    }

    //모임: 실패 허용, 로그만 찍고 빈 리스트 반환
    private List<MeetingPreviewResponse> getRecentMeetings() {
        try {
            return meetingRepository.findTop3ByOrderByCreatedAtDesc()
                    .stream()
                    .map(MeetingPreviewResponse::from)
                    .toList();
        } catch (Exception e) {
            log.error("[Home] 모임 데이터 조회 실패", e);
            return List.of();
        }
    }

    //제휴업체: 실패 허용, 로그만 찍고 빈 리스트 반환
    private List<AffiliateHomePreviewResponse> getAffiliates() {
        try {
            return affiliateService.getAffiliateList();
        } catch (Exception e) {
            log.error("[Home] 제휴 업체 데이터 조회 실패", e);
            return List.of();
        }
    }

    //파워유저: 실패 시 BusinessException 발생 → 클라이언트에게 에러 전달
    private List<PowerUserResponse> getPowerUsersOrThrow() {
        try {
            List<PowerUserResponse> users = powerUserService.getRecentPowerUsers();
            if (users.isEmpty()) {
                throw new BusinessException(HomeError.POWER_USER_NOT_FOUND);
            }
            return users;
        } catch (BusinessException e) {
            // 도메인 예외는 그대로 던짐
            throw e;
        } catch (Exception e) {
            log.error("[Home] 파워 유저 데이터 조회 실패", e);
            throw new BusinessException(HomeError.POWER_USER_NOT_FOUND); // 일반 예외도 통합 처리
        }
    }
}