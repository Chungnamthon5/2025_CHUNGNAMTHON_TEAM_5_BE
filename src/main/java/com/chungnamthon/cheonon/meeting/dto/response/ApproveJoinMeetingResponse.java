package com.chungnamthon.cheonon.meeting.dto.response;

public record ApproveJoinMeetingResponse(
        Long meetingId,
        Long approvedUserId
) {
}
