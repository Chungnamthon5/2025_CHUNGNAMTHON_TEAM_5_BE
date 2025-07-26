package com.chungnamthon.cheonon.home.dto;

import com.chungnamthon.cheonon.meeting.dto.response.MeetingPreviewResponse;
import com.chungnamthon.cheonon.map.dto.AffiliateHomePreviewResponse;
import com.chungnamthon.cheonon.poweruser.dto.PowerUserResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HomeResponse {

    private List<MeetingPreviewResponse> recentMeetings;
    private List<AffiliateHomePreviewResponse> topAffiliates;
    private List<PowerUserResponse> powerUsers;
}