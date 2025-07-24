package com.chungnamthon.cheonon.meeting.dto.response;

import com.chungnamthon.cheonon.meeting.domain.Meeting;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MeetingPreviewDto {

    private Long id;
    private String title;
    private String imageUrl;
    private String location; // Enum을 문자열로
    private String schedule; // Enum을 문자열로

    public static MeetingPreviewDto from(Meeting meeting) {
        return MeetingPreviewDto.builder()
                .id(meeting.getId())
                .title(meeting.getTitle())
                .imageUrl(meeting.getImageUrl())
                .location(meeting.getLocation().name())
                .schedule(meeting.getSchedule().name())
                .build();
    }
}