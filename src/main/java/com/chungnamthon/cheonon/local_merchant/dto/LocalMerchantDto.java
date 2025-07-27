package com.chungnamthon.cheonon.local_merchant.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalMerchantDto {
    private String merchantSeq;
    private String name;
    private String category;
    private String address;
    private String tel;
    private Double latitude;
    private Double longitude;
}