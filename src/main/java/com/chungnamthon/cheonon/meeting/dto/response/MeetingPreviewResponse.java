package com.chungnamthon.cheonon.meeting.dto.response;

import com.chungnamthon.cheonon.meeting.domain.Meeting;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MeetingPreviewResponse {

    private Long id;
    private String title;
    private String imageUrl;
    private String location; // Enum을 문자열로
    private String schedule; // Enum을 문자열로
    private String shortDescription;

    public static MeetingPreviewResponse from(Meeting meeting) {
        return MeetingPreviewResponse.builder()
                .id(meeting.getId())
                .title(meeting.getTitle())
                .imageUrl(meeting.getImageUrl())
                .location(meeting.getLocation().name())
                .schedule(meeting.getSchedule().name())
                .shortDescription(truncateDescription(meeting.getDescription()))
                .build();
    }

    private static String truncateDescription(String description) {
        if (description == null) return "";
        return description.length() > 50 ? description.substring(0, 50) + "..." : description;
    }
}