package com.chungnamthon.cheonon.coupon.service;

import com.chungnamthon.cheonon.coupon.domain.Coupon;
import com.chungnamthon.cheonon.coupon.dto.response.CouponListResponse;
import com.chungnamthon.cheonon.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

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
}
