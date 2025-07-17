package com.chungnamthon.cheonon.meeting.repository;

import com.chungnamthon.cheonon.meeting.domain.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
}
