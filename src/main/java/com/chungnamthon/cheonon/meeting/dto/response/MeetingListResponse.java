package com.chungnamthon.cheonon.meeting.dto.response;

import com.chungnamthon.cheonon.meeting.domain.value.Location;
import com.chungnamthon.cheonon.meeting.domain.value.Schedule;

public record MeetingListResponse(
        Long meetingId,
        boolean isHost,
        String title,
        String description,
        Location location,
        Schedule schedule,
        String imageUrl
) {
}
