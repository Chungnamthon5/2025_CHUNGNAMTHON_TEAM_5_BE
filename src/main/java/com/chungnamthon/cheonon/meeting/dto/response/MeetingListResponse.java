package com.chungnamthon.cheonon.meeting.dto.response;

import com.chungnamthon.cheonon.meeting.domain.value.Location;
import com.chungnamthon.cheonon.meeting.domain.value.Schedule;
import com.chungnamthon.cheonon.meeting.domain.value.Status;

public record MeetingListResponse(
        Long meetingId,
        Status status,
        boolean isHost,
        String title,
        String description,
        Location location,
        Schedule schedule,
        String imageUrl
) {
}
