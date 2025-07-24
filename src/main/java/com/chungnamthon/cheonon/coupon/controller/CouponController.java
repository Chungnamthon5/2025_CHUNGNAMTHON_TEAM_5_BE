package com.chungnamthon.cheonon.coupon.controller;

import com.chungnamthon.cheonon.coupon.dto.response.CouponListResponse;
import com.chungnamthon.cheonon.coupon.service.CouponService;
import com.chungnamthon.cheonon.global.payload.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
}
