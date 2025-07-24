package com.chungnamthon.cheonon.home.service;

import com.chungnamthon.cheonon.home.dto.HomeResponse;
import com.chungnamthon.cheonon.map.dto.AffiliateHomePreviewResponse;
import com.chungnamthon.cheonon.map.service.AffiliateService;
import com.chungnamthon.cheonon.meeting.dto.response.MeetingPreviewResponse;
import com.chungnamthon.cheonon.meeting.repository.MeetingRepository;
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

    public HomeResponse getHomeData() {
        List<MeetingPreviewResponse> recentMeetings;
        try {
            recentMeetings = meetingRepository.findTop3ByOrderByCreatedAtDesc()
                    .stream()
                    .map(MeetingPreviewResponse::from)
                    .toList();
        } catch (Exception e) {
            log.error("홈화면 모임 데이터 조회 실패: {}", e.getMessage());
            recentMeetings = List.of();
        }

        List<AffiliateHomePreviewResponse> topAffiliates;
        try {
            topAffiliates = affiliateService.getTop4Affiliates();
        } catch (Exception e) {
            log.error("홈화면 제휴업체 데이터 조회 실패: {}", e.getMessage());
            topAffiliates = List.of();
        }

        return HomeResponse.builder()
                .recentMeetings(recentMeetings)
                // .topUsers(null) // 추후 구현
                .topAffiliates(topAffiliates)
                .build();
    }
}