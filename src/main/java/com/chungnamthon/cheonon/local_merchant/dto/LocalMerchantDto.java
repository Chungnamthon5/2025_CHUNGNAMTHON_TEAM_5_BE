package com.chungnamthon.cheonon.local_merchant.dto;

import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalMerchantDto {
    private BigInteger merchantSeq;
    private String name;
    private String category;
    private String address;
    private String tel;
    private Double latitude;
    private Double longitude;
}