package com.chungnamthon.cheonon.map.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class MerchantDto {
    private Long id;
    private BigDecimal merchantSeq;
    private String name;
    private String address;
    private String tel;
    private String category;
    private Boolean isAffiliate;
    private Double lat;
    private Double lng;
}
