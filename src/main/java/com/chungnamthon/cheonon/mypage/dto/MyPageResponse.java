package com.chungnamthon.cheonon.mypage.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPageResponse {
    private Long userId;
    private String userName;
    private String profileImageUrl;
    private Integer currentPoint;
    private Integer couponCount;
}
