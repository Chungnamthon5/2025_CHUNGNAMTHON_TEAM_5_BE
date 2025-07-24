package com.chungnamthon.cheonon.map.dto;

import com.chungnamthon.cheonon.map.domain.Affiliate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AffiliatePreviewResponse {
    private String name;
    private String address;
    private String tel;

    public static AffiliatePreviewResponse from(Affiliate a) {
        return AffiliatePreviewResponse.builder()
                .name(a.getName())
                .address(a.getAddress())
                .tel(a.getTel())
                .build();
    }
}
