package com.chungnamthon.cheonon.point.dto.response;

import com.chungnamthon.cheonon.point.domain.PowerUser;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PowerUserResponse {

    private Long userId;
    private String nickname;
    private String image;
    private Integer totalPoint;
    private Integer rank;

    public static PowerUserResponse from(PowerUser powerUser) {
        return PowerUserResponse.builder()
                .userId(powerUser.getUser().getId())
                .nickname(powerUser.getUser().getNickname())
                .image(powerUser.getUser().getImage())
                .totalPoint(powerUser.getTotalPoint())
                .rank(powerUser.getRank())
                .build();
    }
}