package com.chungnamthon.cheonon.coupon.controller;

import com.chungnamthon.cheonon.coupon.dto.request.ExchangeCouponRequest;
import com.chungnamthon.cheonon.coupon.dto.response.CouponListResponse;
import com.chungnamthon.cheonon.coupon.dto.response.ExchangeCouponResponse;
import com.chungnamthon.cheonon.coupon.dto.response.MyCouponListResponse;
import com.chungnamthon.cheonon.global.payload.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "쿠폰 API",
        description = "쿠폰 관련 기능 API입니다."
)
@RequestMapping("/api/coupons")
public interface CouponControllerSwagger {

    @PostMapping("/exchange")
    @Operation(
            summary = "쿠폰 교환",
            description = "사용자가 보유한 포인트를 사용하여 쿠폰을 교환합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "교환할 쿠폰의 ID",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExchangeCouponRequest.class),
                            examples = @ExampleObject(
                                    name = "쿠폰 교환 요청 예시",
                                    value = """
                                            {
                                              "couponId": 1
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "쿠폰 교환 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "쿠폰 교환 성공 응답 예시",
                                            value = """
                                                    {
                                                      "timeStamp": "2025-07-26T12:00:00",
                                                      "message": "Coupon successfully exchanged.",
                                                      "data": {
                                                        "couponId": 1,
                                                        "title": "동대문엽기떡볶이 5000원 할인 쿠폰",
                                                        "point": 5000,
                                                        "expirationDate": "2025-08-26"
                                                      }
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "포인트 부족 또는 이미 발급된 쿠폰",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "포인트 부족 예시",
                                            value = """
                                                    {
                                                      "httpStatus": "BAD_REQUEST",
                                                      "message": "포인트가 부족합니다.",
                                                      "timeStamp": "2025-07-26T12:01:00"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "유저 또는 쿠폰 정보 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "쿠폰 없음 예시",
                                            value = """
                                                    {
                                                      "httpStatus": "NOT_FOUND",
                                                      "message": "존재하지 않는 쿠폰입니다.",
                                                      "timeStamp": "2025-07-26T12:02:00"
                                                    }
                                                    """
                                    )
                            )
                    )
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @Parameter(
            name = "Authorization",
            description = "JWT 토큰 (Bearer 방식)",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", format = "jwt"),
            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    public ResponseDto<ExchangeCouponResponse> exchangeCoupon(
            @RequestHeader("Authorization") String token,
            @RequestBody ExchangeCouponRequest exchangeCouponRequest
    );


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

    @GetMapping("/me")
    @Operation(
            summary = "내 쿠폰 리스트 조회",
            description = "인증된 사용자의 쿠폰 목록을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "내 쿠폰 리스트 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "내 쿠폰 리스트 조회 성공 응답 예시",
                                            value = """
                                                    {
                                                      "timeStamp": "2025-07-25T14:30:00",
                                                      "message": "Successfully retrieved my coupon list.",
                                                      "data": [
                                                        {
                                                          "couponId": 1,
                                                          "userId": 5,
                                                          "title": "동대문엽기떡볶이 5000원 할인 쿠폰",
                                                          "point": 5000,
                                                          "expirationPeriod": "2025-08-24",
                                                          "imageUrl": "https://example.com/coupon1.jpg"
                                                        },
                                                        {
                                                          "couponId": 2,
                                                          "userId": 5,
                                                          "title": "도리하다 3000원 쿠폰",
                                                          "point": 3000,
                                                          "expirationPeriod": "2025-08-29",
                                                          "imageUrl": "https://example.com/coupon2.jpg"
                                                        }
                                                      ]
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패 - 유효하지 않은 토큰",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "인증 실패 예시",
                                            value = """
                                                    {
                                                      "httpStatus": "UNAUTHORIZED",
                                                      "message": "유효하지 않은 토큰입니다.",
                                                      "timeStamp": "2025-07-25T14:31:00"
                                                    }
                                                    """
                                    )
                            )
                    )
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @Parameter(
            name = "Authorization",
            description = "JWT 토큰 (Bearer 방식)",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", format = "jwt"),
            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    ResponseDto<List<MyCouponListResponse>> myCouponList(
            @RequestHeader("Authorization") String token
    );

}
