package com.chungnamthon.cheonon.meeting.controller;

import com.chungnamthon.cheonon.global.payload.ResponseDto;
import com.chungnamthon.cheonon.meeting.dto.request.CreateMeetingRequest;
import com.chungnamthon.cheonon.meeting.dto.request.UpdateMeetingRequest;
import com.chungnamthon.cheonon.meeting.dto.response.CreateMeetingResponse;
import com.chungnamthon.cheonon.meeting.dto.response.UpdateMeetingResponse;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(
        name = "모임 API",
        description = "모임 관련 기능 API입니다."
)
public interface MeetingControllerSwagger {

    @PostMapping
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

    @PatchMapping("/{meetingId}")
    @Operation(
            summary = "모임 정보 수정",
            description = "모임 정보를 수정합니다. JWT 인증이 필요하며, 원하는 필드만 선택적으로 수정할 수 있습니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "모임 수정 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "모임 수정 성공 응답",
                                            value = """
                                            {
                                                "timeStamp": "2025-07-22T18:40:00",
                                                "message": "Successfully updated a meeting information.",
                                                "data": {
                                                    "meetingId": 1
                                                }
                                            }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 - 유효성 검증 실패 등",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "잘못된 요청 예시",
                                            value = """
                                            {
                                                "httpStatus": "BAD_REQUEST",
                                                "message": "스케줄 타입은 필수입니다.",
                                                "timeStamp": "2025-07-22T18:41:00"
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
    @Parameter(
            name = "meetingId",
            description = "수정할 모임의 ID",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "integer", format = "int64"),
            example = "1"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "수정할 모임 정보 (필수 아님 - 일부 필드만 전송 가능)",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UpdateMeetingRequest.class),
                    examples = @ExampleObject(
                            name = "모임 수정 요청 예시",
                            value = """
                            {
                                "title": "수정된 모임 제목",
                                "description": "모임 설명을 업데이트합니다.",
                                "location": "MOKCHEON",
                                "openChatUrl": "https://open.kakao.com/o/gdHuVjIh",
                                "schedule": "WEEKEND",
                                "imageUrl": "https://example.com/new-image.jpg"
                            }
                        """
                    )
            )
    )
    ResponseDto<UpdateMeetingResponse> updateMeeting(
            @RequestHeader("Authorization") String token,
            @PathVariable("meetingId") Long meetingId,
            @RequestBody @Valid UpdateMeetingRequest updateMeetingRequest
    );
}
