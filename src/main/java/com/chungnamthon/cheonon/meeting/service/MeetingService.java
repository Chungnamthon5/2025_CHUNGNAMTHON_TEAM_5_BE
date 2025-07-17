package com.chungnamthon.cheonon.meeting.service;

import com.chungnamthon.cheonon.meeting.domain.Meeting;
import com.chungnamthon.cheonon.meeting.domain.MeetingUser;
import com.chungnamthon.cheonon.meeting.domain.value.Location;
import com.chungnamthon.cheonon.meeting.domain.value.Role;
import com.chungnamthon.cheonon.meeting.dto.request.CreateMeetingRequest;
import com.chungnamthon.cheonon.meeting.dto.response.CreateMeetingResponse;
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
    // private final UserRepository userRepository;


    /**
     * 모임 생성 메서드
     * @param token
     * @param createMeetingRequest
     * @return createMeetingResponse (meetingId)
     */
    @Transactional
    public CreateMeetingResponse createMeeting(String token, CreateMeetingRequest createMeetingRequest) {
        // Todo jwt로 user_id 추출
        // User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(AuthenticationError.USER_NOT_FOUND));

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
}

