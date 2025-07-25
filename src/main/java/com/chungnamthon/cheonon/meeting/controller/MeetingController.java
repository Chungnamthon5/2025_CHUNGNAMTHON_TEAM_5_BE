package com.chungnamthon.cheonon.meeting.controller;

import com.chungnamthon.cheonon.global.payload.ResponseDto;
import com.chungnamthon.cheonon.meeting.dto.request.CreateMeetingRequest;
import com.chungnamthon.cheonon.meeting.dto.request.UpdateMeetingRequest;
import com.chungnamthon.cheonon.meeting.dto.response.*;
import com.chungnamthon.cheonon.meeting.service.MeetingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController implements MeetingControllerSwagger {

    private final MeetingService meetingService;

    @PostMapping
    public ResponseDto<CreateMeetingResponse> createMeeting(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid CreateMeetingRequest createMeetingRequest
    ) {
        CreateMeetingResponse createMeetingResponse = meetingService.createMeeting(token, createMeetingRequest);
        return ResponseDto.of(createMeetingResponse, "The meeting was created successfully.");
    }

    @PostMapping("/{meetingId}/join")
    public ResponseDto<JoinMeetingResponse> joinMeeting(
            @RequestHeader("Authorization") String token,
            @PathVariable("meetingId") Long meetingId
    ) {
        JoinMeetingResponse joinMeetingResponse = meetingService.joinMeeting(token, meetingId);
        return ResponseDto.of(joinMeetingResponse, "You have successfully applied to join the group.");
    }

    @PostMapping("/{meetingId}/approve/{userId}")
    public ResponseDto<ApproveJoinMeetingResponse> approveJoinMeeting(
            @RequestHeader("Authorization") String token,
            @PathVariable("meetingId") Long meetingId,
            @PathVariable("userId") Long userId
    ) {
        ApproveJoinMeetingResponse approveJoinMeetingResponse = meetingService.approveJoinMeeting(token, meetingId, userId);
        return ResponseDto.of(approveJoinMeetingResponse, "The user has been approved to join the meeting.");
    }

    @GetMapping
    public ResponseDto<List<MeetingListResponse>> meetingList(
            @RequestHeader(name = "Authorization", required = false) String token
    ) {
        List<MeetingListResponse> meetingListResponse = meetingService.getMeetingList(token);
        return ResponseDto.of(meetingListResponse, "Successfully retrieved meeting list.");
    }

    @GetMapping("/{meetingId}")
    public ResponseDto<MeetingDetailResponse> meetingDetail(
            @RequestHeader(name = "Authorization", required = false) String token,
            @PathVariable("meetingId") Long meetingId
    ) {
        MeetingDetailResponse meetingDetailResponse = meetingService.getMeetingDetailInformation(token, meetingId);
        return ResponseDto.of(meetingDetailResponse, "Successfully retrieved meeting detail.");
    }

    @GetMapping("/{meetingId}/users")
    public ResponseDto<List<MeetingUsersListResponse>> meetingUsersList(
            @RequestHeader("Authorization") String token,
            @PathVariable("meetingId") Long meetingId
    ) {
        List<MeetingUsersListResponse> meetingUsersListResponses = meetingService.getmeetingUsersList(token, meetingId);
        return ResponseDto.of(meetingUsersListResponses, "Successfully retrieved meeting members.");
    }

    @PatchMapping("/{meetingId}")
    public ResponseDto<UpdateMeetingResponse> updateMeeting(
            @RequestHeader("Authorization") String token,
            @PathVariable("meetingId") Long meetingId,
            @RequestBody @Valid UpdateMeetingRequest updateMeetingRequest
    ) {
        UpdateMeetingResponse updateMeetingResponse = meetingService.updateMeetingInformation(token, meetingId, updateMeetingRequest);
        return ResponseDto.of(updateMeetingResponse, "Successfully updated a meeting information.");
    }

    @DeleteMapping("/{meetingId}")
    public ResponseDto<Long> deleteMeeting(
            @RequestHeader("Authorization") String token,
            @PathVariable("meetingId") Long meetingId
    ) {
        meetingService.deleteMeeting(token, meetingId);
        return ResponseDto.of(meetingId, "Successfully deleted the meeting.");
    }

    @DeleteMapping("/{meetingId}/cancel")
    public ResponseDto<CancelJoinMeetingResponse> cancelJoinMeeting(
            @RequestHeader("Authorization") String token,
            @PathVariable("meetingId") Long meetingId
    ) {
        CancelJoinMeetingResponse cancelJoinMeetingResponse = meetingService.cancelJoinMeeting(token, meetingId);
        return ResponseDto.of(cancelJoinMeetingResponse, "Your join request has been successfully cancelled.");
    }

    @DeleteMapping("/{meetingId}/leave")
    public ResponseDto<LeaveMeetingResponse> leaveMeeting(
            @RequestHeader("Authorization") String token,
            @PathVariable("meetingId") Long meetingId
    ) {
        LeaveMeetingResponse leaveMeetingResponse = meetingService.leaveMeeting(token, meetingId);
        return ResponseDto.of(leaveMeetingResponse, "You have successfully left the meeting.");
    }

    @DeleteMapping("/{meetingId}/reject/{userId}")
    public ResponseDto<RejectMeetingResponse> rejectJoinMeeting(
            @RequestHeader("Authorization") String token,
            @PathVariable("meetingId") Long meetingId,
            @PathVariable("userId") Long userId
    ) {
        RejectMeetingResponse rejectMeetingResponse = meetingService.rejectMeeting(token, meetingId, userId);
        return ResponseDto.of(rejectMeetingResponse, "The user's join request has been rejected.");
    }
}
