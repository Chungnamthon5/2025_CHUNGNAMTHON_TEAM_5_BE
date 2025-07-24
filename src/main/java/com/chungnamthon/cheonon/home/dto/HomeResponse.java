package com.chungnamthon.cheonon.home.dto;

import com.chungnamthon.cheonon.meeting.dto.response.MeetingPreviewDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HomeResponse {

    private List<MeetingPreviewDto> recentMeetings;
    // 이후 주석 처리해서 확장 준비
    // private List<PowerUserDto> top5PowerUsers;
    // private List<MerchantPreviewDto> top5Merchants;
}