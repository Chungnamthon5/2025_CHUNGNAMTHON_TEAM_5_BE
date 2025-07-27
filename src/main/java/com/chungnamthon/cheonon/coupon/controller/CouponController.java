package com.chungnamthon.cheonon.coupon.controller;

import com.chungnamthon.cheonon.coupon.dto.request.ExchangeCouponRequest;
import com.chungnamthon.cheonon.coupon.dto.request.UseCouponRequest;
import com.chungnamthon.cheonon.coupon.dto.response.CouponListResponse;
import com.chungnamthon.cheonon.coupon.dto.response.ExchangeCouponResponse;
import com.chungnamthon.cheonon.coupon.dto.response.MyCouponListResponse;
import com.chungnamthon.cheonon.coupon.dto.response.UseCouponResponse;
import com.chungnamthon.cheonon.coupon.service.CouponService;
import com.chungnamthon.cheonon.global.payload.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController implements CouponControllerSwagger {

    private final CouponService couponService;

    @PostMapping("/exchange")
    public ResponseDto<ExchangeCouponResponse> exchangeCoupon(
            @RequestHeader("Authorization") String token,
            @RequestBody ExchangeCouponRequest exchangeCouponRequest
    ) {
        ExchangeCouponResponse exchangeCouponResponse = couponService.exchangeCoupon(token, exchangeCouponRequest);
        return ResponseDto.of(exchangeCouponResponse, "Coupon successfully exchanged.");
    }

    @PostMapping("/use")
    public ResponseDto<UseCouponResponse> useCoupon(
            @RequestHeader("Authorization") String token,
            @RequestBody UseCouponRequest useCouponRequest
    ) {
        UseCouponResponse useCouponResponse = couponService.useCoupon(token, useCouponRequest);
        return ResponseDto.of(useCouponResponse, "Coupon successfully used.");
    }

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
