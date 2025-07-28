package com.chungnamthon.cheonon.meeting.repository;

import com.chungnamthon.cheonon.meeting.domain.Meeting;
import com.chungnamthon.cheonon.meeting.domain.MeetingUser;
import com.chungnamthon.cheonon.meeting.domain.value.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MeetingUserRepository extends JpaRepository<MeetingUser, Long> {
    List<MeetingUser> findByMeetingId(Long meetingId);

    MeetingUser findByUserIdAndMeetingId(Long userId, Long meetingId);

    List<MeetingUser> findByUserIdAndStatusIn(Long userId, List<Status> statuses);

    List<MeetingUser> findByUserIdAndStatus(Long userId, Status status);

    @Query("SELECT COUNT(mu) FROM MeetingUser mu WHERE mu.user.id = :userId AND mu.createdAt BETWEEN :start AND :end")
    int countMeetingParticipationByUserIdAndPeriod(@Param("userId") Long userId,
                                                   @Param("start") LocalDateTime start,
                                                   @Param("end") LocalDateTime end);
}
