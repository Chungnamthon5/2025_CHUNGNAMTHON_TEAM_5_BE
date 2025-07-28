package com.chungnamthon.cheonon.map.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchResultDto {
    private Long id;
    private String name;
    private String address;
    private String tel;
    private String category;
    private Double lat;
    private Double lng;
    private boolean isAffiliate;
}
