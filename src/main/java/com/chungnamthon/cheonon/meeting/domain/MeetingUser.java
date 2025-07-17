package com.chungnamthon.cheonon.meeting.domain;

import com.chungnamthon.cheonon.meeting.domain.value.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class MeetingUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

/*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
*/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    @Enumerated(EnumType.STRING)
    private Role role;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    protected MeetingUser() {
    }

    @Builder
    public MeetingUser(/*User user,*/ Meeting meeting, Role role) {
//        this.user = user;
        this.meeting = meeting;
        this.role = role;
    }
}
