package com.chungnamthon.cheonon.meeting.controller;

import com.chungnamthon.cheonon.global.payload.ResponseDto;
import com.chungnamthon.cheonon.meeting.dto.request.CreateMeetingRequest;
import com.chungnamthon.cheonon.meeting.dto.request.UpdateMeetingRequest;
import com.chungnamthon.cheonon.meeting.dto.response.CreateMeetingResponse;
import com.chungnamthon.cheonon.meeting.dto.response.MeetingDetailResponse;
import com.chungnamthon.cheonon.meeting.dto.response.MeetingListResponse;
import com.chungnamthon.cheonon.meeting.dto.response.UpdateMeetingResponse;
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

    @GetMapping
    public ResponseDto<List<MeetingListResponse>> meetingList() {
        List<MeetingListResponse> meetingListResponse = meetingService.getMeetingList();
        return ResponseDto.of(meetingListResponse, "Successfully retrieved meeting list.");
    }

    @GetMapping("/{meetingId}")
    public ResponseDto<MeetingDetailResponse> meetingDetail(
            @PathVariable("meetingId") Long meetingId
    ) {
        MeetingDetailResponse meetingDetailResponse = meetingService.getMeetingDetailInformation(meetingId);
        return ResponseDto.of(meetingDetailResponse, "Successfully retrieved meeting detail.");
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
}
