package com.chungnamthon.cheonon.point.controller;

import com.chungnamthon.cheonon.global.payload.ResponseDto;
import com.chungnamthon.cheonon.point.dto.response.PointHistoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(
        name = "포인트 API",
        description = "포인트 적립 및 사용 내역 조회 API입니다."
)
@RequestMapping("/api/points")
public interface PointControllerSwagger {

    @Operation(
            summary = "내 포인트 사용 내역 조회",
            description = "인증된 사용자의 포인트 적립 및 사용 내역을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "포인트 내역 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "포인트 내역 조회 응답 예시",
                                            value = """
                                                    {
                                                      "timeStamp": "2025-07-25T15:00:00",
                                                      "message": "Successfully retrieved point history.",
                                                      "data": [
                                                        {
                                                          "pointId": 1,
                                                          "paymentType": "MEETING_CREATION",
                                                          "changedPoint": 20,
                                                          "usedAt": "2025-07-20T15:24:00"
                                                        },
                                                        {
                                                          "pointId": 2,
                                                          "paymentType": "EXCHANGE_COUPON",
                                                          "changedPoint": -500,
                                                          "usedAt": "2025-07-21T10:11:00"
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
                                                      "timeStamp": "2025-07-25T15:01:00"
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
    @GetMapping
    ResponseDto<List<PointHistoryResponse>> pointHistory(
            @RequestHeader("Authorization") String token
    );
}
