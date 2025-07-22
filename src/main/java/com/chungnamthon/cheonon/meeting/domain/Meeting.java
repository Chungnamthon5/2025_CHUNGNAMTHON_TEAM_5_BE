package com.chungnamthon.cheonon.meeting.domain;

import com.chungnamthon.cheonon.global.domain.BaseEntity;
import com.chungnamthon.cheonon.meeting.domain.value.Location;
import com.chungnamthon.cheonon.meeting.domain.value.Schedule;
import com.chungnamthon.cheonon.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Meeting extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "location")
    private Location location;

    @Column(name = "openchat_url")
    private String openChatUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule")
    private Schedule schedule;

    @Column(name = "image_url")
    private String imageUrl;

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateLocation(Location location) {
        this.location = location;
    }

    public void updatedOpenChatUrl(String openChatUrl) {
        this.openChatUrl = openChatUrl;
    }

    public void updatedScheduleType(Schedule schedule) {
        this.schedule = schedule;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}