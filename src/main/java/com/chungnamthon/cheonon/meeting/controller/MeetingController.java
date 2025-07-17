package com.chungnamthon.cheonon.meeting.controller;

import com.chungnamthon.cheonon.global.payload.ResponseDto;
import com.chungnamthon.cheonon.meeting.dto.request.CreateMeetingRequest;
import com.chungnamthon.cheonon.meeting.dto.response.CreateMeetingResponse;
import com.chungnamthon.cheonon.meeting.service.MeetingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController implements MeetingControllerSwagger {

    private final MeetingService meetingService;

    @PostMapping
    public ResponseDto<CreateMeetingResponse> createMeeting(
            @RequestHeader("Authorization") String token, // 임시
            @RequestBody @Valid CreateMeetingRequest createMeetingRequest
    ) {
        CreateMeetingResponse createMeetingResponse = meetingService.createMeeting(token, createMeetingRequest);
        return ResponseDto.of(createMeetingResponse, "The meeting was created successfully.");
    }
}
