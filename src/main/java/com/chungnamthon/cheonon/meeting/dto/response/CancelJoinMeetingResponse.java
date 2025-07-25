package com.chungnamthon.cheonon.meeting.dto.response;

public record CancelJoinMeetingResponse(
        Long meetingId,
        Long cancelUserId
) {
}
