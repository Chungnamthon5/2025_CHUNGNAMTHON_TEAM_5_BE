package com.chungnamthon.cheonon.map.dto;


import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class AffiliateDto {
    private Long id;
    private BigDecimal merchantSeq;
    private String name;
    private String address;
    private String tel;
    private String category;
    private Double lat;
    private Double lng;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
