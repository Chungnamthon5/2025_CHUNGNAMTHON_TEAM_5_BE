package com.chungnamthon.cheonon.meeting.dto.request;

import com.chungnamthon.cheonon.meeting.domain.value.Location;
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

        @NotNull(message = "인원 설정은 필수입니다.")
        @Min(value = 2, message = "최소 2명 이상이어야 합니다.")
        @Max(value = 10, message = "최대 10명까지 가능합니다.")
        Integer maxMember,

        @Schema(nullable = true)
        String imageUrl
) {
}
