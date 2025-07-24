package com.chungnamthon.cheonon.coupon.controller;

import com.chungnamthon.cheonon.coupon.dto.response.CouponListResponse;
import com.chungnamthon.cheonon.global.payload.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(
        name = "쿠폰 API",
        description = "쿠폰 관련 기능 API입니다."
)
@RequestMapping("/api/coupons")
public interface CouponControllerSwagger {

    @GetMapping
    @Operation(
            summary = "쿠폰 리스트 조회",
            description = "모든 쿠폰 목록을 조회합니다. 인증 없이 접근할 수 있습니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "쿠폰 리스트 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "쿠폰 리스트 조회 응답 예시",
                                            value = """
                                                    {
                                                      "timeStamp": "2025-07-25T12:00:00",
                                                      "message": "Successfully retrieved coupon list.",
                                                      "data": [
                                                        {
                                                          "couponId": 1,
                                                          "title": "동대문엽기떡볶이 5000원 할인 쿠폰",
                                                          "point": 5000,
                                                          "expirationPeriod": 30,
                                                          "image": "https://example.com/coupon1.jpg"
                                                        },
                                                        {
                                                          "couponId": 2,
                                                          "title": "도리하다 3000원 쿠폰",
                                                          "point": 3000,
                                                          "expirationPeriod": 30,
                                                          "image": "https://example.com/coupon2.jpg"
                                                        }
                                                      ]
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    ResponseDto<List<CouponListResponse>> couponList();
}
