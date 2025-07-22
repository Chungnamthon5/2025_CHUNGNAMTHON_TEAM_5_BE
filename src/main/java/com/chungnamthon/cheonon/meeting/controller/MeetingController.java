package com.chungnamthon.cheonon.meeting.controller;

import com.chungnamthon.cheonon.global.payload.ResponseDto;
import com.chungnamthon.cheonon.meeting.dto.request.CreateMeetingRequest;
import com.chungnamthon.cheonon.meeting.dto.request.UpdateMeetingRequest;
import com.chungnamthon.cheonon.meeting.dto.response.CreateMeetingResponse;
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
            @RequestHeader(value = "Authorization", required = false) String token, // Todo JWT 이슈 해결 완료 후 required 삭제
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

    @PatchMapping("/{meetingId}")
    public ResponseDto<UpdateMeetingResponse> updateMeeting(
            @RequestHeader(value = "Authorization", required = false) String token, // Todo JWT 이슈 해결 완료 후 required 삭제
            @PathVariable("meetingId") Long meetingId,
            @RequestBody @Valid UpdateMeetingRequest updateMeetingRequest
    ) {
        UpdateMeetingResponse updateMeetingResponse = meetingService.updateMeetingInformation(token, meetingId, updateMeetingRequest);
        return ResponseDto.of(updateMeetingResponse, "Successfully updated a meeting information.");
    }
}
