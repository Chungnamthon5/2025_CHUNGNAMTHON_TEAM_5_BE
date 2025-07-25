package com.chungnamthon.cheonon.meeting.dto.response;

public record KickMemberMeetingResponse(
        Long meetingId,
        Long kickedUserId
) {
}
