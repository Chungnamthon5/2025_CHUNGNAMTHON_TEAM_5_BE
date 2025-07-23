package com.chungnamthon.cheonon.meeting.service;

import com.chungnamthon.cheonon.auth.jwt.JwtUtil;
import com.chungnamthon.cheonon.global.exception.BusinessException;
import com.chungnamthon.cheonon.global.exception.error.AuthenticationError;
import com.chungnamthon.cheonon.global.exception.error.MeetingError;
import com.chungnamthon.cheonon.meeting.domain.Meeting;
import com.chungnamthon.cheonon.meeting.domain.MeetingUser;
import com.chungnamthon.cheonon.meeting.domain.value.Location;
import com.chungnamthon.cheonon.meeting.domain.value.Role;
import com.chungnamthon.cheonon.meeting.domain.value.Schedule;
import com.chungnamthon.cheonon.meeting.dto.request.CreateMeetingRequest;
import com.chungnamthon.cheonon.meeting.dto.request.UpdateMeetingRequest;
import com.chungnamthon.cheonon.meeting.dto.response.CreateMeetingResponse;
import com.chungnamthon.cheonon.meeting.dto.response.MeetingDetailResponse;
import com.chungnamthon.cheonon.meeting.dto.response.MeetingListResponse;
import com.chungnamthon.cheonon.meeting.dto.response.UpdateMeetingResponse;
import com.chungnamthon.cheonon.meeting.repository.MeetingRepository;
import com.chungnamthon.cheonon.meeting.repository.MeetingUserRepository;
import com.chungnamthon.cheonon.user.domain.User;
import com.chungnamthon.cheonon.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingUserRepository meetingUserRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /**
     * 모임 생성 메서드
     * @param token
     * @param createMeetingRequest
     * @return createMeetingResponse (meetingId)
     */
    @Transactional
    public CreateMeetingResponse createMeeting(String token, CreateMeetingRequest createMeetingRequest) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(AuthenticationError.USER_NOT_FOUND));

        String title = createMeetingRequest.title();
        String description = createMeetingRequest.description();
        Location location = createMeetingRequest.location();
        String openChatUrl = createMeetingRequest.openChatUrl();
        Schedule schedule = createMeetingRequest.schedule();
        String imageUrl = createMeetingRequest.imageUrl();

        Meeting meeting = Meeting.builder()
                .user(user)
                .title(title)
                .description(description)
                .location(location)
                .openChatUrl(openChatUrl)
                .schedule(schedule)
                .imageUrl(imageUrl)
                .build();

        MeetingUser meetingUser = MeetingUser.builder()
                .meeting(meeting)
                .user(user)
                .role(Role.HOST)
                .build();

        meetingRepository.save(meeting);
        meetingUserRepository.save(meetingUser);

        return new CreateMeetingResponse(meeting.getId());
    }

    /**
     * 모임 리스트 조회 메서드
     * @return meetingListResponse (전체 리스트)
     */
    public List<MeetingListResponse> getMeetingList() {
        List<Meeting> meetingList = meetingRepository.findAll();

        List<MeetingListResponse> meetingListResponses = new ArrayList<>();
        for (Meeting meeting : meetingList) {
            Long meetingId = meeting.getId();
            String title = meeting.getTitle();
            String description = meeting.getDescription();
            Location location = meeting.getLocation();
            Schedule schedule = meeting.getSchedule();
            String imageUrl = meeting.getImageUrl();

            MeetingListResponse meetingListResponse
                    = new MeetingListResponse(meetingId, title, description, location, schedule, imageUrl);
            meetingListResponses.add(meetingListResponse);
        }

        return meetingListResponses;
    }

    /**
     * 모임 상세 정보 조회
     * @param meetingId
     * @return meetingDetailResponse (meetingId, title, description, location, schedule, imageUrl, openChatUrl)
     */
    public MeetingDetailResponse getMeetingDetailInformation(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new BusinessException(MeetingError.MEETING_NOT_FOUND));

        String title = meeting.getTitle();
        String description = meeting.getDescription();
        Location location = meeting.getLocation();
        Schedule schedule = meeting.getSchedule();
        String imageUrl = meeting.getImageUrl();
        String openChatUrl = meeting.getOpenChatUrl();

        MeetingDetailResponse meetingDetailResponse
                = new MeetingDetailResponse(meetingId, title, description, location, schedule, imageUrl, openChatUrl);

        return meetingDetailResponse;
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
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new BusinessException(MeetingError.MEETING_NOT_FOUND));

        Long userId = jwtUtil.getUserIdFromToken(token);
        if (!meeting.getUser().getId().equals(userId)) {
            throw new BusinessException(MeetingError.FORBIDDEN_MEETING_UPDATE);
        }

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

        if (updateMeetingRequest.openChatUrl() != null) {
            if (updateMeetingRequest.openChatUrl().isBlank()) {
                throw new BusinessException(MeetingError.INVALID_DESCRIPTION);
            }
            meeting.updatedOpenChatUrl(updateMeetingRequest.openChatUrl());
        }

        if (updateMeetingRequest.schedule() != null) {
            meeting.updatedScheduleType(updateMeetingRequest.schedule());
        }

        if (updateMeetingRequest.imageUrl() != null) {
            meeting.updateImageUrl(updateMeetingRequest.imageUrl());
        }

        return new UpdateMeetingResponse(meeting.getId());
    }

    @Transactional
    public void deleteMeeting(String token, Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new BusinessException(MeetingError.MEETING_NOT_FOUND));

        Long userId = jwtUtil.getUserIdFromToken(token);
        if (!meeting.getUser().getId().equals(userId)) {
            throw new BusinessException(MeetingError.FORBIDDEN_MEETING_DELETE);
        }

        meetingRepository.deleteById(meetingId);
    }
}
