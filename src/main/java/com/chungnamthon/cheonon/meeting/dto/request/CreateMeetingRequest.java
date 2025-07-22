package com.chungnamthon.cheonon.meeting.dto.request;

import com.chungnamthon.cheonon.meeting.domain.value.Location;
import com.chungnamthon.cheonon.meeting.domain.value.Schedule;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record CreateMeetingRequest(

        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 24, message = "제목은 24자 이내여야 합니다.")
        String title,

        @NotBlank(message = "설명은 필수입니다.")
        @Size(max = 500, message = "설명은 500자 이내여야 합니다.")
        String description,

        @NotNull(message = "위치는 필수입니다.")
        Location location,

        // ex. https://open.kakao.com/o/gq9lWjIh
        @Pattern(
                regexp = "^https://open\\.kakao\\.com/o/[A-Za-z0-9]+$",
                message = "올바른 카카오 오픈채팅방 링크 형식이 아닙니다."
        )
        @NotBlank(message = "오픈채팅방 링크는 필수입니다.")
        String openChatUrl,

        @NotNull(message = "스케줄 타입은 필수입니다.")
        Schedule schedule,

        @Schema(nullable = true)
        String imageUrl
) {
}
