package com.chungnamthon.cheonon.coupon.repository;

import com.chungnamthon.cheonon.coupon.domain.CouponUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponUserRepository extends JpaRepository<CouponUser, Long> {
    List<CouponUser> findByUserId(Long userId);

    int countByUser_Id(Long userId);

    boolean existsByUserIdAndCouponId(Long userId, Long couponId);

    CouponUser findByUserIdAndCouponId(Long userId, Long couponId);
}
