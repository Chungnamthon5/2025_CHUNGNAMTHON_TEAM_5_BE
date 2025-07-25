package com.chungnamthon.cheonon.meeting.dto.response;

public record RejectMeetingResponse(
        Long meetingId,
        Long rejectedUserId
) {
}
