package com.chungnamthon.cheonon.coupon.service;

import com.chungnamthon.cheonon.auth.jwt.JwtUtil;
import com.chungnamthon.cheonon.coupon.domain.Coupon;
import com.chungnamthon.cheonon.coupon.domain.CouponUser;
import com.chungnamthon.cheonon.coupon.dto.response.CouponListResponse;
import com.chungnamthon.cheonon.coupon.dto.response.MyCouponListResponse;
import com.chungnamthon.cheonon.coupon.repository.CouponRepository;
import com.chungnamthon.cheonon.coupon.repository.CouponUserRepository;
import com.chungnamthon.cheonon.global.exception.BusinessException;
import com.chungnamthon.cheonon.global.exception.error.CouponError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponUserRepository couponUserRepository;
    private final JwtUtil jwtUtil;

    public List<CouponListResponse> getCouponList() {
        List<Coupon> couponList = couponRepository.findAll();

        List<CouponListResponse> couponListResponses = new ArrayList<>();
        for (Coupon coupon : couponList) {
            Long id = coupon.getId();
            String title = coupon.getTitle();
            Integer point = coupon.getPoint();
            Integer expirationPeriod = coupon.getExpirationPeriod();
            String imageUrl = coupon.getImageUrl();

            CouponListResponse couponListResponse
                    = new CouponListResponse(id, title, point, expirationPeriod, imageUrl);
            couponListResponses.add(couponListResponse);
        }

        return couponListResponses;
    }

    public List<MyCouponListResponse> getMyCouponList(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        List<CouponUser> myCoupons = couponUserRepository.findByUserId(userId);

        List<Coupon> coupons = new ArrayList<>();
        for (CouponUser myCoupon : myCoupons) {
            Long couponId = myCoupon.getCoupon().getId();
            Coupon coupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> new BusinessException(CouponError.COUPON_NOT_FOUND));
            coupons.add(coupon);
        }

        List<MyCouponListResponse> myCouponListResponses = new ArrayList<>();
        for (Coupon myCoupon : coupons) {
            Long id = myCoupon.getId(); // 쿠폰의 id (내 쿠폰 id x)
            String title = myCoupon.getTitle();
            Integer point = myCoupon.getPoint();
            LocalDate expirationPeriod = myCoupon.getCreatedAt().toLocalDate().plusDays(myCoupon.getExpirationPeriod());
            String imageUrl = myCoupon.getImageUrl();

            MyCouponListResponse myCouponListResponse
                    = new MyCouponListResponse(id, userId, title, point, expirationPeriod, imageUrl);

            myCouponListResponses.add(myCouponListResponse);
        }

        return myCouponListResponses;
    }
}
