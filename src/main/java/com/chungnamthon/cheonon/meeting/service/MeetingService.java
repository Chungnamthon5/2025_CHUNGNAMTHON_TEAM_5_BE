package com.chungnamthon.cheonon.meeting.service;

import com.chungnamthon.cheonon.auth.jwt.JwtUtil;
import com.chungnamthon.cheonon.global.exception.BusinessException;
import com.chungnamthon.cheonon.global.exception.error.AuthenticationError;
import com.chungnamthon.cheonon.global.exception.error.MeetingError;
import com.chungnamthon.cheonon.meeting.domain.Meeting;
import com.chungnamthon.cheonon.meeting.domain.MeetingUser;
import com.chungnamthon.cheonon.meeting.domain.value.Location;
import com.chungnamthon.cheonon.meeting.domain.value.Schedule;
import com.chungnamthon.cheonon.meeting.domain.value.Status;
import com.chungnamthon.cheonon.meeting.dto.request.CreateMeetingRequest;
import com.chungnamthon.cheonon.meeting.dto.request.UpdateMeetingRequest;
import com.chungnamthon.cheonon.meeting.dto.response.*;
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
     *
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
                .status(Status.HOST)
                .build();

        meetingRepository.save(meeting);
        meetingUserRepository.save(meetingUser);

        return new CreateMeetingResponse(meeting.getId());
    }

    /**
     * 모임 가입 신청 메서드
     * @param token
     * @param meetingId
     * @return JoinMeetingResponse (meetingId)
     */
    @Transactional
    public JoinMeetingResponse joinMeeting(String token, Long meetingId) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(AuthenticationError.USER_NOT_FOUND));

        MeetingUser meetingUser = meetingUserRepository.findByUserIdAndMeetingId(userId, meetingId);
        if (meetingUser.getStatus().equals(Status.HOST) || meetingUser.getStatus().equals(Status.PARTICIPATING)) {
            throw new BusinessException(MeetingError.ALREADY_JOINED_MEETING);
        } else if (meetingUser.getStatus().equals(Status.KICKED)) {
            throw new BusinessException(MeetingError.KICKED_USER_CANNOT_REJOIN);
        }

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new BusinessException(MeetingError.MEETING_NOT_FOUND));

        meetingUser = MeetingUser.builder()
                .meeting(meeting)
                .user(user)
                .status(Status.REQUESTED)
                .build();

        meetingUserRepository.save(meetingUser);

        return new JoinMeetingResponse(meetingId);
    }

    /**
     * 모임 가입 신청 승인 메서드
     * @param token
     * @param meetingId
     * @param userId
     * @return ApproveJoinMeetingResponse (meetingId, approvedUserId)
     */
    @Transactional
    public ApproveJoinMeetingResponse approveJoinMeeting(String token, Long meetingId, Long userId) {
        Long hostId = jwtUtil.getUserIdFromToken(token);

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new BusinessException(MeetingError.MEETING_NOT_FOUND));

        if (!hostId.equals(meeting.getUser().getId())) {
            throw new BusinessException(MeetingError.FORBIDDEN_MEETING_MEMBER_MANAGEMENT);
        }

        MeetingUser meetingUser = meetingUserRepository.findByUserIdAndMeetingId(userId, meetingId);
        if (meetingUser.getStatus().equals(Status.REQUESTED)) {
            meetingUser.approveJoin(Status.PARTICIPATING);
        } else {
            throw new BusinessException(MeetingError.NOT_A_PARTICIPATING_MEMBER);
        }

        return new ApproveJoinMeetingResponse(meetingId, userId);
    }

    /**
     * 모임 멤버 강퇴 메서드
     * @param token
     * @param meetingId
     * @param userId
     * @return KickMemberMeetingResponse (meetingId, kickedUserId)
     */
    @Transactional
    public KickMemberMeetingResponse kickMemberMeeting(String token, Long meetingId, Long userId) {
        Long hostId = jwtUtil.getUserIdFromToken(token);

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new BusinessException(MeetingError.MEETING_NOT_FOUND));

        if (!hostId.equals(meeting.getUser().getId())) {
            throw new BusinessException(MeetingError.FORBIDDEN_MEETING_MEMBER_MANAGEMENT);
        }

        MeetingUser meetingUser = meetingUserRepository.findByUserIdAndMeetingId(userId, meetingId);
        if (meetingUser.getStatus().equals(Status.PARTICIPATING)) {
            meetingUser.approveJoin(Status.KICKED);
        } else {
            throw new BusinessException(MeetingError.NOT_A_PARTICIPATING_MEMBER);
        }

        return new KickMemberMeetingResponse(meetingId, userId);
    }

    /**
     * 모임 리스트 조회 메서드
     *
     * @return meetingListResponse (전체 리스트)
     */
    public List<MeetingListResponse> getMeetingList(String token) {
        List<Meeting> meetingList = meetingRepository.findAll();

        Long userId = null;
        if (token != null) {
            userId = jwtUtil.getUserIdFromToken(token);
        }

        List<MeetingListResponse> meetingListResponses = new ArrayList<>();
        for (Meeting meeting : meetingList) {
            Long meetingId = meeting.getId();
            boolean isHost = false;
            if (userId != null) {
                isHost = userId.equals(meeting.getUser().getId());
            }
            String title = meeting.getTitle();
            String description = meeting.getDescription();
            Location location = meeting.getLocation();
            Schedule schedule = meeting.getSchedule();
            String imageUrl = meeting.getImageUrl();

            MeetingListResponse meetingListResponse
                    = new MeetingListResponse(meetingId, isHost, title, description, location, schedule, imageUrl);
            meetingListResponses.add(meetingListResponse);
        }

        return meetingListResponses;
    }

    /**
     * 모임 상세 정보 조회
     *
     * @param meetingId
     * @return meetingDetailResponse (meetingId, title, description, location, schedule, imageUrl, openChatUrl)
     */
    public MeetingDetailResponse getMeetingDetailInformation(String token, Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new BusinessException(MeetingError.MEETING_NOT_FOUND));

        Long userId = null;
        if (token != null) {
            userId = jwtUtil.getUserIdFromToken(token);
        }

        boolean isHost = false;
        if (userId != null) {
            isHost = userId.equals(meeting.getUser().getId());
        }

        String hostName = meeting.getUser().getNickname();

        String title = meeting.getTitle();
        String description = meeting.getDescription();
        Location location = meeting.getLocation();
        Schedule schedule = meeting.getSchedule();
        String imageUrl = meeting.getImageUrl();
        String openChatUrl = meeting.getOpenChatUrl();

        MeetingDetailResponse meetingDetailResponse
                = new MeetingDetailResponse(meetingId, isHost, hostName, title, description, location, schedule, imageUrl, openChatUrl);

        return meetingDetailResponse;
    }

    /**
     * 모임 멤버 리스트 메서드
     * @param token
     * @param meetingId
     * @return List<MeetingUsersListResponse> (userId, userNickName, userImageUrl, status)
     */
    public List<MeetingUsersListResponse> getmeetingUsersList(String token, Long meetingId) {
        Long hostId = jwtUtil.getUserIdFromToken(token);

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new BusinessException(MeetingError.MEETING_NOT_FOUND));

        if (!hostId.equals(meeting.getUser().getId())) {
            throw new BusinessException(MeetingError.FORBIDDEN_MEETING_MEMBER_ACCESS);
        }

        List<MeetingUser> meetingUsers = meetingUserRepository.findByMeetingId(meetingId);

        List<MeetingUsersListResponse> meetingUsersListResponses = new ArrayList<>();
        for (MeetingUser meetingUser : meetingUsers) {
            Status status = meetingUser.getStatus();
            if (status.equals(Status.HOST) || status.equals(Status.REJECTED) || status.equals(Status.KICKED)) {
                continue;
            }

            Long userId = meetingUser.getUser().getId();
            String nickname = meetingUser.getUser().getNickname();
            String imageUrl = meetingUser.getUser().getImage();

            MeetingUsersListResponse meetingUsersListResponse
                    = new MeetingUsersListResponse(userId, nickname, imageUrl, status);

            meetingUsersListResponses.add(meetingUsersListResponse);
        }

        return meetingUsersListResponses;
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

    /**
     * 모임 삭제 메서드
     * @param token
     * @param meetingId
     */
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

    /**
     * 모임 가입 신청 취소
     *
     * @param token
     * @param meetingId
     * @return CancelJoinMeetingResponse (meetingId, userId)
     */
    @Transactional
    public CancelJoinMeetingResponse cancelJoinMeeting(String token, Long meetingId) {
        Long userId = jwtUtil.getUserIdFromToken(token);

        MeetingUser meetingUser = meetingUserRepository.findByUserIdAndMeetingId(userId, meetingId);

        Long meetingUserId = meetingUser.getId();
        if (meetingUser.getStatus().equals(Status.REQUESTED)) {
            meetingUserRepository.deleteById(meetingUserId);
        } else {
            throw new BusinessException(MeetingError.INVALID_JOIN_REQUEST_STATE);
        }

        return new CancelJoinMeetingResponse(meetingId, userId);
    }

    /**
     * 모임 나가기 메서드
     * @param token
     * @param meetingId
     * @return LeaveMeetingResponse (meetingUser, leftUserId)
     */
    @Transactional
    public LeaveMeetingResponse leaveMeeting(String token, Long meetingId) {
        Long userId = jwtUtil.getUserIdFromToken(token);

        MeetingUser meetingUser = meetingUserRepository.findByUserIdAndMeetingId(userId, meetingId);

        Long meetingUserId = meetingUser.getId();
        if (meetingUser.getStatus().equals(Status.PARTICIPATING)) {
            meetingUserRepository.deleteById(meetingUserId);
        } else if (meetingUser.getStatus().equals(Status.HOST)) {
            throw new BusinessException(MeetingError.HOST_CANNOT_LEAVE_MEETING);
        } else {
            throw new BusinessException(MeetingError.NOT_JOINED_MEETING);
        }

        return new LeaveMeetingResponse(meetingId, userId);
    }

    /**
     * 모임 가입 신청 거절 메서드
     * @param token
     * @param meetingId
     * @param userId
     * @return RejectMeetingResponse (meetingId, rejectedUserId)
     */
    @Transactional
    public RejectMeetingResponse rejectMeeting(String token, Long meetingId, Long userId) {
        Long hostId = jwtUtil.getUserIdFromToken(token);

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new BusinessException(MeetingError.MEETING_NOT_FOUND));

        if (!hostId.equals(meeting.getUser().getId())) {
            throw new BusinessException(MeetingError.FORBIDDEN_MEETING_MEMBER_MANAGEMENT);
        }

        MeetingUser meetingUser = meetingUserRepository.findByUserIdAndMeetingId(userId, meetingId);
        Long meetingUserId = meetingUser.getId();
        if (meetingUser.getStatus().equals(Status.REQUESTED)) {
            meetingUserRepository.deleteById(meetingUserId);
        } else {
            throw new BusinessException(MeetingError.NOT_A_PARTICIPATING_MEMBER);
        }

        return new RejectMeetingResponse(meetingId, userId);
    }
}
