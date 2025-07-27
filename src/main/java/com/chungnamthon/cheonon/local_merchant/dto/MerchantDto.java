package com.chungnamthon.cheonon.local_merchant.dto;

import com.chungnamthon.cheonon.local_merchant.domain.Merchant;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    public static MerchantDto fromEntity(Merchant m) {
        return MerchantDto.builder()
                .id(m.getId())
                .merchantSeq(m.getMerchantSeq())
                .name(m.getName())
                .category(m.getCategory())
                .address(m.getAddress())
                .tel(m.getTel())
                .lat(m.getLatitude())
                .lng(m.getLongitude())
                .isAffiliate(m.getIsAffiliate())
                .build();
    }
}
