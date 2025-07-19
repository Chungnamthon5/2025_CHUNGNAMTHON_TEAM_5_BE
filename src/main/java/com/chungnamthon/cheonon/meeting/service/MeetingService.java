package com.chungnamthon.cheonon.meeting.service;

import com.chungnamthon.cheonon.global.exception.BusinessException;
import com.chungnamthon.cheonon.global.exception.error.MeetingError;
import com.chungnamthon.cheonon.meeting.domain.Meeting;
import com.chungnamthon.cheonon.meeting.domain.MeetingUser;
import com.chungnamthon.cheonon.meeting.domain.value.Location;
import com.chungnamthon.cheonon.meeting.domain.value.Role;
import com.chungnamthon.cheonon.meeting.dto.request.CreateMeetingRequest;
import com.chungnamthon.cheonon.meeting.dto.request.UpdateMeetingRequest;
import com.chungnamthon.cheonon.meeting.dto.response.CreateMeetingResponse;
import com.chungnamthon.cheonon.meeting.dto.response.UpdateMeetingResponse;
import com.chungnamthon.cheonon.meeting.repository.MeetingRepository;
import com.chungnamthon.cheonon.meeting.repository.MeetingUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingUserRepository meetingUserRepository;

    /**
     * 모임 생성 메서드
     * @param token
     * @param createMeetingRequest
     * @return createMeetingResponse (meetingId)
     */
    @Transactional
    public CreateMeetingResponse createMeeting(String token, CreateMeetingRequest createMeetingRequest) {
        // Todo JWT 이슈 해결 완료 후 userId 추출 로직 추가
        /*Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(AuthenticationError.USER_NOT_FOUND));*/

        String title = createMeetingRequest.title();
        String description = createMeetingRequest.description();
        Location location = createMeetingRequest.location();
        int maxMember = createMeetingRequest.maxMember();
        String imageUrl = createMeetingRequest.imageUrl();

        Meeting meeting = Meeting.builder()
                .title(title)
                .description(description)
                .location(location)
                .maxMember(maxMember)
                .imageUrl(imageUrl)
                .totalMemberCount(1)
                .build();

        MeetingUser meetingUser = MeetingUser.builder()
                .meeting(meeting)
                .role(Role.HOST)
                .build();

        meetingRepository.save(meeting);
        meetingUserRepository.save(meetingUser);

        return new CreateMeetingResponse(meeting.getId());
    }

    /**
     * 모임 정보 수정 메서드
     * @param token
     * @param meetingId
     * @param updateMeetingRequest
     * @return updateMeetingResponse (meetingId)
     */
    @Transactional
    public UpdateMeetingResponse updateMeetingInformation(String token, Long meetingId, UpdateMeetingRequest updateMeetingRequest) {
        // Todo JWT 이슈 해결 완료 후 userId 추출 로직 추가

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new BusinessException(MeetingError.MEETING_NOT_FOUND));

        if (updateMeetingRequest.title() != null) {
            if (updateMeetingRequest.title().isBlank()) {
                throw new BusinessException(MeetingError.INVALID_TITLE);
            }
            meeting.updateTitle(updateMeetingRequest.title());
        }

        if (updateMeetingRequest.description() != null) {
            if (updateMeetingRequest.description().isBlank()) {
                throw new BusinessException(MeetingError.INVALID_DESCRIPTION);
            }
            meeting.updateDescription(updateMeetingRequest.description());
        }

        if (updateMeetingRequest.location() != null) {
            meeting.updateLocation(updateMeetingRequest.location());
        }

        if (updateMeetingRequest.maxMember() != null) {
            meeting.updateMaxMember(updateMeetingRequest.maxMember());
        }

        if (updateMeetingRequest.imageUrl() != null) {
            meeting.updateImageUrl(updateMeetingRequest.imageUrl());
        }

        return new UpdateMeetingResponse(meeting.getId());
    }
}

