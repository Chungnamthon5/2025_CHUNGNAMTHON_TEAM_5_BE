package com.chungnamthon.cheonon.meeting.dto.response;

public record LeaveMeetingResponse(
        Long meetingId,
        Long leftUserId
) {
}
