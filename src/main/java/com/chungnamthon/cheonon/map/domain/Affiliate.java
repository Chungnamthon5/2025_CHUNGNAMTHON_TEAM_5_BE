package com.chungnamthon.cheonon.map.domain;

import com.chungnamthon.cheonon.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "affiliate_store")
@Getter
@Setter
public class Affiliate extends BaseEntity {

    @Column(name = "merchant_seq")
    private BigDecimal merchantSeq;

    private String name;
    private String address;
    private String tel;
    private String category;


    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;


}
