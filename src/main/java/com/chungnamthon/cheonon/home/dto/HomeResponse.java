package com.chungnamthon.cheonon.home.dto;

import com.chungnamthon.cheonon.meeting.dto.response.MeetingPreviewResponse;
import com.chungnamthon.cheonon.map.dto.AffiliateHomePreviewResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HomeResponse {

    private List<MeetingPreviewResponse> recentMeetings;
    private List<AffiliateHomePreviewResponse> topAffiliates;

    // 이후 확장용 주석
    // private List<PowerUserDto> top5PowerUsers;
}