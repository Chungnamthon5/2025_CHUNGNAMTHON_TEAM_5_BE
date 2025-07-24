package com.chungnamthon.cheonon.map.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "merchant")
@Getter
@Setter
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "merchant_seq")
    private BigDecimal merchantSeq;   // 원천 식별번호

    private String name;
    private String address;
    private String tel;
    private String category;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "is_affiliate")
    private Boolean isAffiliate;      // 제휴 여부

}
