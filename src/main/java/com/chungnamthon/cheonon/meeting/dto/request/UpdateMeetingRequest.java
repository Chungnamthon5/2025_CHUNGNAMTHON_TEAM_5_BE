package com.chungnamthon.cheonon.meeting.dto.request;

import com.chungnamthon.cheonon.meeting.domain.value.Location;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateMeetingRequest(

        @Size(max = 24, message = "제목은 24자 이내여야 합니다.")
        String title,

        @Size(max = 500, message = "설명은 500자 이내여야 합니다.")
        String description,

        Location location,

        @Min(value = 2, message = "최소 2명 이상이어야 합니다.")
        @Max(value = 10, message = "최대 10명까지 가능합니다.")
        Integer maxMember,

        String imageUrl
) {
}
