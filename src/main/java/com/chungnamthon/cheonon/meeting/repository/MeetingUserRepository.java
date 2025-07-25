package com.chungnamthon.cheonon.meeting.repository;

import com.chungnamthon.cheonon.meeting.domain.MeetingUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingUserRepository extends JpaRepository<MeetingUser, Long> {
    List<MeetingUser> findByMeetingId(Long meetingId);

    MeetingUser findByUserIdAndMeetingId(Long userId, Long meetingId);
}
