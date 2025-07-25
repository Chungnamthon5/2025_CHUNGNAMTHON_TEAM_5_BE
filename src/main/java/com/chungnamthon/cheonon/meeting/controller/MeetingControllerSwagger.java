package com.chungnamthon.cheonon.meeting.controller;

import com.chungnamthon.cheonon.global.payload.ResponseDto;
import com.chungnamthon.cheonon.meeting.dto.request.CreateMeetingRequest;
import com.chungnamthon.cheonon.meeting.dto.request.UpdateMeetingRequest;
import com.chungnamthon.cheonon.meeting.dto.response.*;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/{meetingId}/join")
    @Operation(
            summary = "모임 가입 신청",
            description = "모임에 가입 신청을 합니다. 이미 가입 중이거나 신청한 경우 예외가 발생할 수 있습니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "가입 신청 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "가입 신청 성공 응답",
                                            value = """
                                                        {
                                                            "timeStamp": "2025-07-25T04:38:30.8827509",
                                                            "message": "You have successfully applied to join the group.",
                                                            "data": {
                                                                "meetingId": 44
                                                            }
                                                        }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "이미 가입 또는 신청된 모임",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "가입 중복 에러",
                                            value = """
                                                        {
                                                            "httpStatus": "BAD_REQUEST",
                                                            "message": "이미 가입 중이거나, 신청하지 않은 모임입니다.",
                                                            "timeStamp": "2025-07-25T04:40:00"
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
            description = "가입 신청할 모임 ID",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "integer", format = "int64"),
            example = "44"
    )
    ResponseDto<JoinMeetingResponse> joinMeeting(
            @RequestHeader("Authorization") String token,
            @PathVariable("meetingId") Long meetingId
    );

    @PostMapping("/{meetingId}/approve/{userId}")
    @Operation(
            summary = "모임 가입 신청 승인",
            description = "호스트가 신청자의 가입을 승인합니다. 신청 상태가 'REQUESTED'인 경우에만 승인됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "가입 승인 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "가입 승인 성공 응답 예시",
                                            value = """
                                                    {
                                                      "timeStamp": "2025-07-25T22:45:00",
                                                      "message": "The user has been approved to join the meeting.",
                                                      "data": {
                                                        "meetingId": 1,
                                                        "approvedUserId": 3
                                                      }
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "호스트가 아님 / 승인 권한 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "호스트 권한 없음",
                                            value = """
                                                    {
                                                      "httpStatus": "FORBIDDEN",
                                                      "message": "본인이 생성한 모임의 멤버만 관리할 수 있습니다.",
                                                      "timeStamp": "2025-07-25T22:46:00"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 신청 상태",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "승인 불가 상태",
                                            value = """
                                                    {
                                                      "httpStatus": "BAD_REQUEST",
                                                      "message": "해당 모임에 참여 중인 멤버가 아닙니다.",
                                                      "timeStamp": "2025-07-25T22:47:00"
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
            description = "가입을 승인할 모임 ID",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "integer", format = "int64"),
            example = "1"
    )
    @Parameter(
            name = "userId",
            description = "가입을 승인할 사용자 ID",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "integer", format = "int64"),
            example = "3"
    )
    ResponseDto<ApproveJoinMeetingResponse> approveJoinMeeting(
            @RequestHeader("Authorization") String token,
            @PathVariable("meetingId") Long meetingId,
            @PathVariable("userId") Long userId
    );

    @PostMapping("/{meetingId}/kick/{userId}")
    @Operation(
            summary = "모임 멤버 강퇴",
            description = "호스트가 모임의 참여 멤버를 강퇴합니다. 상태가 'PARTICIPATING'인 멤버만 강퇴 가능하며, 상태는 'KICKED'로 변경됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "강퇴 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "강퇴 성공 응답 예시",
                                            value = """
                                                    {
                                                      "timeStamp": "2025-07-25T04:38:30.8827509",
                                                      "message": "The user has been removed from the meeting.",
                                                      "data": {
                                                        "meetingId": 44,
                                                        "kickedUserId": 103
                                                      }
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "호스트가 아님 또는 강퇴 권한 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "호스트 권한 없음",
                                            value = """
                                                    {
                                                      "httpStatus": "FORBIDDEN",
                                                      "message": "본인이 생성한 모임의 멤버만 관리할 수 있습니다.",
                                                      "timeStamp": "2025-07-26T15:01:00"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "참여 중이 아닌 멤버",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "강퇴 불가 상태",
                                            value = """
                                                    {
                                                      "httpStatus": "FORBIDDEN",
                                                      "message": "해당 모임에 참여 중인 멤버가 아닙니다.",
                                                      "timeStamp": "2025-07-26T15:02:00"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않는 모임",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "모임 없음 에러",
                                            value = """
                                                    {
                                                      "httpStatus": "NOT_FOUND",
                                                      "message": "존재하지 않는 모임입니다.",
                                                      "timeStamp": "2025-07-26T15:03:00"
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
            description = "강퇴할 유저가 포함된 모임 ID",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "integer", format = "int64"),
            example = "44"
    )
    @Parameter(
            name = "userId",
            description = "강퇴할 사용자 ID",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "integer", format = "int64"),
            example = "103"
    )
    ResponseDto<KickMemberMeetingResponse> kickMemberMeeting(
            @RequestHeader("Authorization") String token,
            @PathVariable("meetingId") Long meetingId,
            @PathVariable("userId") Long userId
    );


    @GetMapping
    @Operation(
            summary = "모임 리스트 조회",
            description = "전체 모임 목록을 조회합니다. 로그인하지 않은 사용자도 접근할 수 있으며, 로그인한 경우 본인이 생성한 모임인지 여부를 isHost로 함께 반환합니다. (본인이 생성한 모임일 시 true, else false)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "모임 리스트 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "모임 리스트 조회 성공 응답 예시",
                                            value = """
                                                    {
                                                        "timeStamp": "2025-07-22T22:45:00",
                                                        "message": "Successfully retrieved meeting list.",
                                                        "data": [
                                                            {
                                                                "meetingId": 1,
                                                                "isHost": true,
                                                                "title": "천안역 커피 미팅",
                                                                "description": "같이 커피 마시면서 네트워킹해요!",
                                                                "location": "MOKCHEON",
                                                                "schedule": "WEEKEND",
                                                                "imageUrl": "https://example.com/image1.jpg"
                                                            },
                                                            {
                                                                "meetingId": 2,
                                                                "isHost": false,
                                                                "title": "테니스 치실 분",
                                                                "description": "라켓 있으신 분 환영",
                                                                "location": "MOKCHEON",
                                                                "schedule": "WEEKDAY",
                                                                "imageUrl": "https://example.com/image2.jpg"
                                                            }
                                                        ]
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    @Parameter(
            name = "Authorization",
            description = "JWT 토큰 (Bearer 방식). 선택 항목입니다. 로그인한 경우 본인이 생성한 모임 여부가 반영됩니다.",
            required = false,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", format = "jwt"),
            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    ResponseDto<List<MeetingListResponse>> meetingList(
            @RequestHeader(name = "Authorization", required = false) String token
    );

    @GetMapping("/{meetingId}")
    @Operation(
            summary = "모임 상세 정보 조회",
            description = "모임 ID를 통해 상세 정보를 조회합니다. 인증 없이 접근할 수 있습니다. 로그인한 경우 본인이 생성한 모임인지 여부와 생성자의 닉네임도 함께 반환됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "모임 상세 정보 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "모임 상세 정보 응답 예시",
                                            value = """
                                                    {
                                                      "timeStamp": "2025-07-23T10:30:00",
                                                      "message": "Successfully retrieved meeting detail.",
                                                      "data": {
                                                        "meetingId": 1,
                                                        "isHost": true,
                                                        "hostName": "천안유저",
                                                        "title": "천안역 커피 미팅",
                                                        "description": "같이 커피 마시면서 네트워킹해요!",
                                                        "location": "MOKCHEON",
                                                        "schedule": "WEEKEND",
                                                        "imageUrl": "https://example.com/meeting.jpg",
                                                        "openChatUrl": "https://open.kakao.com/o/abc123"
                                                      }
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않는 모임 ID",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "모임 없음 에러",
                                            value = """
                                                    {
                                                      "httpStatus": "NOT_FOUND",
                                                      "message": "존재하지 않는 모임입니다.",
                                                      "timeStamp": "2025-07-23T10:32:00"
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    @Parameter(
            name = "Authorization",
            description = "JWT 토큰 (Bearer 방식). 선택 항목입니다. 로그인한 경우 본인이 생성한 모임인지 여부가 반영됩니다.",
            required = false,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", format = "jwt"),
            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    @Parameter(
            name = "meetingId",
            description = "조회할 모임의 ID",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "integer", format = "int64"),
            example = "1"
    )
    ResponseDto<MeetingDetailResponse> meetingDetail(
            @RequestHeader(name = "Authorization", required = false) String token,
            @PathVariable("meetingId") Long meetingId
    );

    @GetMapping("/{meetingId}/users")
    @Operation(
            summary = "모임 멤버 리스트 조회",
            description = "해당 모임의 멤버 목록을 조회합니다. 생성자만 접근할 수 있으며, 각 멤버의 상태(Status)를 통해 신청 대기/참여/거절 등을 구분할 수 있습니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "모임 멤버 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "모임 멤버 리스트 응답 예시",
                                            value = """
                                                        {
                                                          "timeStamp": "2025-07-25T12:30:00",
                                                          "message": "Successfully retrieved meeting members.",
                                                          "data": [
                                                            {
                                                              "userId": 10,
                                                              "userNickName": "예린",
                                                              "userImageUrl": "https://example.com/user.jpg",
                                                              "status": "REQUESTED"
                                                            },
                                                            {
                                                              "userId": 12,
                                                              "userNickName": "홍길동",
                                                              "userImageUrl": "https://example.com/user2.jpg",
                                                              "status": "PARTICIPATING"
                                                            }
                                                          ]
                                                        }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "접근 권한 없음 (호스트 아님)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "접근 권한 없음 에러",
                                            value = """
                                                        {
                                                          "httpStatus": "FORBIDDEN",
                                                          "message": "본인이 생성한 모임의 멤버 리스트만 조회할 수 있습니다.",
                                                          "timeStamp": "2025-07-25T12:31:00"
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
            description = "멤버를 조회할 모임 ID",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "integer", format = "int64"),
            example = "1"
    )
    ResponseDto<List<MeetingUsersListResponse>> meetingUsersList(
            @RequestHeader("Authorization") String token,
            @PathVariable("meetingId") Long meetingId
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

    @DeleteMapping("/{meetingId}")
    @Operation(
            summary = "모임 삭제",
            description = "모임을 삭제합니다. JWT 인증이 필요하며, 모임 생성자만 삭제할 수 있습니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "모임 삭제 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "모임 삭제 성공 응답",
                                            value = """
                                                    {
                                                        "timeStamp": "2025-07-23T13:45:00",
                                                        "message": "The meeting was deleted successfully.",
                                                        "data": null
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "삭제 권한 없음 - 본인이 생성한 모임이 아님",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "삭제 권한 없음 에러",
                                            value = """
                                                    {
                                                        "httpStatus": "FORBIDDEN",
                                                        "message": "본인의 모임 게시글만 수정할 수 있습니다.",
                                                        "timeStamp": "2025-07-23T13:46:00"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않는 모임 ID",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "모임 없음 에러",
                                            value = """
                                                    {
                                                        "httpStatus": "NOT_FOUND",
                                                        "message": "존재하지 않는 모임입니다.",
                                                        "timeStamp": "2025-07-23T13:47:00"
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
            description = "삭제할 모임의 ID",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "integer", format = "int64"),
            example = "1"
    )
    ResponseDto<Long> deleteMeeting(
            @RequestHeader("Authorization") String token,
            @PathVariable("meetingId") Long meetingId
    );

    @DeleteMapping("/{meetingId}/cancel")
    @Operation(
            summary = "모임 가입 신청 취소",
            description = "가입 신청한 모임에서 신청을 취소합니다. 'REQUESTED' 상태에서만 취소할 수 있습니다. 토큰 필수.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "가입 신청 취소 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "가입 신청 취소 성공 예시",
                                            value = """
                                                    {
                                                        "timeStamp": "2025-07-25T04:38:30.8827509",
                                                        "message": "Your join request has been successfully cancelled.",
                                                        "data": {
                                                            "meetingId": 44,
                                                            "cancelUserId": 7
                                                        }
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "이미 승인된 모임이거나 신청하지 않았습니다.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "취소 불가 상태 에러 예시",
                                            value = """
                                                    {
                                                        "httpStatus": "BAD_REQUEST",
                                                        "message": "이미 가입 중이거나, 신청하지 않은 모임입니다.",
                                                        "timeStamp": "2025-07-25T04:40:00"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않는 모임 또는 유저",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "모임 없음 에러",
                                            value = """
                                                    {
                                                        "httpStatus": "NOT_FOUND",
                                                        "message": "존재하지 않는 모임입니다.",
                                                        "timeStamp": "2025-07-25T04:41:00"
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
            description = "가입 신청 취소할 모임 ID",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "integer", format = "int64"),
            example = "44"
    )
    ResponseDto<CancelJoinMeetingResponse> cancelJoinMeeting(
            @RequestHeader("Authorization") String token,
            @PathVariable("meetingId") Long meetingId
    );

    @DeleteMapping("/{meetingId}/leave")
    @Operation(
            summary = "모임 탈퇴",
            description = "참여 중인 모임에서 탈퇴합니다. 호스트는 탈퇴할 수 없으며, 탈퇴하려면 모임을 삭제해야 합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "모임 탈퇴 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "모임 탈퇴 성공 예시",
                                            value = """
                                                    {
                                                      "timeStamp": "2025-07-25T22:10:00",
                                                      "message": "You have successfully left the meeting.",
                                                      "data": {
                                                        "meetingId": 5,
                                                        "leftUserId": 3
                                                      }
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "호스트는 모임 탈퇴 불가",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "호스트 탈퇴 불가 예시",
                                            value = """
                                                    {
                                                      "httpStatus": "FORBIDDEN",
                                                      "message": "호스트는 모임을 탈퇴할 수 없습니다. 모임을 삭제해 주세요.",
                                                      "timeStamp": "2025-07-25T22:11:00"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "참여자가 아님",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "참여자가 아님 예시",
                                            value = """
                                                    {
                                                      "httpStatus": "FORBIDDEN",
                                                      "message": "참여하고 있지 않은 모임입니다.",
                                                      "timeStamp": "2025-07-25T22:12:00"
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
            description = "탈퇴할 모임 ID",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "integer", format = "int64"),
            example = "5"
    )
    ResponseDto<LeaveMeetingResponse> leaveMeeting(
            @RequestHeader("Authorization") String token,
            @PathVariable("meetingId") Long meetingId
    );

    @DeleteMapping("/{meetingId}/reject/{userId}")
    @Operation(
            summary = "모임 가입 신청 거절",
            description = "호스트가 신청자의 모임 가입 요청을 거절합니다. 신청 상태가 'REQUESTED'인 경우에만 삭제됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "가입 거절 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "가입 거절 성공 응답 예시",
                                            value = """
                                                    {
                                                      "timeStamp": "2025-07-26T15:00:00",
                                                      "message": "The user has been rejected from the meeting.",
                                                      "data": {
                                                        "meetingId": 1,
                                                        "rejectedUserId": 5
                                                      }
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "호스트가 아님 / 거절 권한 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "호스트 권한 없음",
                                            value = """
                                                    {
                                                      "httpStatus": "FORBIDDEN",
                                                      "message": "본인이 생성한 모임의 멤버만 관리할 수 있습니다.",
                                                      "timeStamp": "2025-07-26T15:01:00"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "요청 상태가 아님",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "요청 상태 아님",
                                            value = """
                                                    {
                                                      "httpStatus": "FORBIDDEN",
                                                      "message": "해당 모임에 참여 중인 멤버가 아닙니다.",
                                                      "timeStamp": "2025-07-26T15:02:00"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않는 모임",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "모임 없음 에러",
                                            value = """
                                                    {
                                                      "httpStatus": "NOT_FOUND",
                                                      "message": "존재하지 않는 모임입니다.",
                                                      "timeStamp": "2025-07-26T15:03:00"
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
            description = "거절할 대상이 있는 모임 ID",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "integer", format = "int64"),
            example = "1"
    )
    @Parameter(
            name = "userId",
            description = "거절할 유저 ID",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "integer", format = "int64"),
            example = "5"
    )
    ResponseDto<RejectMeetingResponse> rejectJoinMeeting(
            @RequestHeader("Authorization") String token,
            @PathVariable("meetingId") Long meetingId,
            @PathVariable("userId") Long userId
    );

}
