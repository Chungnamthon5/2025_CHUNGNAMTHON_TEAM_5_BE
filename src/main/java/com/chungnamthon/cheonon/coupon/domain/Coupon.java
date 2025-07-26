package com.chungnamthon.cheonon.coupon.domain;

import com.chungnamthon.cheonon.global.domain.BaseEntity;
import com.chungnamthon.cheonon.local_merchant.domain.Merchant;
import jakarta.persistence.*;
import lombok.Getter;

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

    @Column(name = "image_url")
    String ImageUrl;
}
