package com.chungnamthon.cheonon.meeting.dto.request;

import com.chungnamthon.cheonon.meeting.domain.value.Location;
import com.chungnamthon.cheonon.meeting.domain.value.Schedule;
import jakarta.validation.constraints.*;

public record UpdateMeetingRequest(

        @Size(max = 24, message = "제목은 24자 이내여야 합니다.")
        String title,

        @Size(max = 500, message = "설명은 500자 이내여야 합니다.")
        String description,

        Location location,

        @Pattern(
                regexp = "^https://open\\.kakao\\.com/o/[A-Za-z0-9]+$",
                message = "올바른 카카오 오픈채팅방 링크 형식이 아닙니다."
        )
        String openChatUrl,

        Schedule schedule,

        String imageUrl
) {
}
