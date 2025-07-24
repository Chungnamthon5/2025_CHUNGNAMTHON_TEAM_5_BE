package com.chungnamthon.cheonon.map.dto;

import com.chungnamthon.cheonon.map.domain.Affiliate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AffiliateHomePreviewResponse {
    private String name;

    public static AffiliateHomePreviewResponse from(Affiliate a) {
        return AffiliateHomePreviewResponse.builder()
                .name(a.getName())
                .build();
    }
}
