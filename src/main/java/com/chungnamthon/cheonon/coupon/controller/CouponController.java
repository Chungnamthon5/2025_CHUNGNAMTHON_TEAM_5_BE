package com.chungnamthon.cheonon.coupon.controller;

import com.chungnamthon.cheonon.coupon.dto.response.CouponListResponse;
import com.chungnamthon.cheonon.coupon.dto.response.MyCouponListResponse;
import com.chungnamthon.cheonon.coupon.service.CouponService;
import com.chungnamthon.cheonon.global.payload.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController implements CouponControllerSwagger {

    private final CouponService couponService;

    @GetMapping
    public ResponseDto<List<CouponListResponse>> couponList() {
        List<CouponListResponse> couponListResponses = couponService.getCouponList();
        return ResponseDto.of(couponListResponses, "Successfully retrieved coupon list.");
    }

    @GetMapping("/me")
    public ResponseDto<List<MyCouponListResponse>> myCouponList(
            @RequestHeader("Authorization") String token
    ) {
        List<MyCouponListResponse> myCouponListResponses = couponService.getMyCouponList(token);
        return ResponseDto.of(myCouponListResponses, "Successfully retrieved my coupon list.");
    }
}
