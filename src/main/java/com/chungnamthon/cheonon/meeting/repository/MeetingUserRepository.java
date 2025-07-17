package com.chungnamthon.cheonon.meeting.repository;

import com.chungnamthon.cheonon.meeting.domain.MeetingUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingUserRepository extends JpaRepository<MeetingUser, Long> {
}
