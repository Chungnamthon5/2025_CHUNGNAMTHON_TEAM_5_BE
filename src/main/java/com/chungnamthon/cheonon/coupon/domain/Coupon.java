package com.chungnamthon.cheonon.coupon.domain;

import com.chungnamthon.cheonon.global.domain.BaseEntity;
import com.chungnamthon.cheonon.map.domain.Merchant;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Table(name = "coupon")
@Getter
public class Coupon extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    Merchant merchant;

    @Column(name = "title")
    String title;

    @Column(name = "point")
    Integer point;

    @Column(name = "expiration_period")
    Integer expirationPeriod;

    @Column(name = "expiration_date")
    LocalDate expirationDate;

    @Column(name = "image_url")
    String ImageUrl;
}
