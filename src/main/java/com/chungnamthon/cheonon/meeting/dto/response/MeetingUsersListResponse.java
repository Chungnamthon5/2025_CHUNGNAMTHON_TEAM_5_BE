package com.chungnamthon.cheonon.meeting.dto.response;

import com.chungnamthon.cheonon.meeting.domain.value.Status;

public record MeetingUsersListResponse(
        Long userId,
        String userNickName,
        String userImageUrl,
        Status status
) {
}
