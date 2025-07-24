package com.chungnamthon.cheonon.meeting.repository;

import com.chungnamthon.cheonon.meeting.domain.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findTop3ByOrderByCreatedAtDesc();
}
