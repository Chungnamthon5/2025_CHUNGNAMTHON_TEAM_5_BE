package com.chungnamthon.cheonon.coupon.domain;

import com.chungnamthon.cheonon.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupon_user")
@Getter
@EntityListeners(AuditingEntityListener.class)
public class CouponUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "expiration_period")
    LocalDate expirationPeriod;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
