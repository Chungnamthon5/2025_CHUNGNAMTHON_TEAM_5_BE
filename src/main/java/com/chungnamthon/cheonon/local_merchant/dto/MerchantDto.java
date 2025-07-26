package com.chungnamthon.cheonon.local_merchant.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MerchantDto {
    private Long id;
    private String merchantSeq;
    private String name;
    private String address;
    private String tel;
    private String category;
    private Boolean isAffiliate;
    private Double lat;
    private Double lng;
}
