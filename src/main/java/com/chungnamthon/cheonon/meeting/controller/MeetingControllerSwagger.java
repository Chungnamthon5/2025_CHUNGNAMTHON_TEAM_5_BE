package com.chungnamthon.cheonon.meeting.controller;

import com.chungnamthon.cheonon.global.payload.ResponseDto;
import com.chungnamthon.cheonon.meeting.dto.request.CreateMeetingRequest;
import com.chungnamthon.cheonon.meeting.dto.response.CreateMeetingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(
        name = "모임 API",
        description = "모임 관련 기능 API입니다."
)
public interface MeetingControllerSwagger {

    @Operation(
            summary = "모임 생성",
            description = "새로운 모임을 생성합니다. JWT 인증이 필요하며, 생성자는 자동으로 HOST로 등록됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "모임 생성 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "모임 생성 성공 응답",
                                            value = """
                                                {
                                                    "timeStamp": "2025-07-17T22:45:00",
                                                    "message": "The meeting was created successfully.",
                                                    "data": {
                                                        "meetingId": 1
                                                    }
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
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "인증 실패 에러",
                                            value = """
                                                {
                                                    "httpStatus": "UNAUTHORIZED",
                                                    "message": "유효하지 않은 토큰입니다.",
                                                    "timeStamp": "2025-07-17T22:46:00"
                                                }
                                                """
                                    )
                            )
                    )
            },
            security = @SecurityRequirement(name = "bearerAuth") // 여기 수정!
    )
    @Parameter(
            name = "Authorization",
            description = "JWT 토큰 (Bearer 방식)",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", format = "jwt"),
            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    @RequestBody(
            description = "모임 생성 요청 데이터",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CreateMeetingRequest.class),
                    examples = @ExampleObject(
                            name = "모임 생성 요청 예시",
                            value = """
                                {
                                    "title": "천안역 커피 미팅",
                                    "description": "같이 커피 마시면서 네트워킹해요!",
                                    "location": "MOKCHEON",
                                    "maxMember": 4,
                                    "imageUrl": "https://example.com/meeting.jpg"
                                }
                        """
                    )
            )
    )
    ResponseDto<CreateMeetingResponse> createMeeting(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid CreateMeetingRequest createMeetingRequest
    );

}
