package com.chungnamthon.cheonon.home.service;

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
                .recentMeetings(getRecentMeetingsSafely())
                .topAffiliates(getAffiliatesSafely())
                .powerUsers(getPowerUsersSafely())
                .build();
    }

    private List<MeetingPreviewResponse> getRecentMeetingsSafely() {
        try {
            return meetingRepository.findTop3ByOrderByCreatedAtDesc()
                    .stream()
                    .map(MeetingPreviewResponse::from)
                    .toList();
        } catch (Exception e) {
            log.error("❗ [Home] 모임 데이터 조회 실패", e);
            return List.of();
        }
    }

    private List<AffiliateHomePreviewResponse> getAffiliatesSafely() {
        try {
            return affiliateService.getAffiliateList();
        } catch (Exception e) {
            log.error("❗ [Home] 제휴업체 데이터 조회 실패", e);
            return List.of();
        }
    }

    private List<PowerUserResponse> getPowerUsersSafely() {
        try {
            return powerUserService.getRecentPowerUsers();
        } catch (Exception e) {
            log.error("❗ [Home] 파워유저 데이터 조회 실패", e);
            return List.of();
        }
    }
}