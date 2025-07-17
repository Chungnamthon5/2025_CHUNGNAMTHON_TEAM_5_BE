package com.chungnamthon.cheonon.meeting.domain;

import com.chungnamthon.cheonon.global.domain.BaseEntity;
import com.chungnamthon.cheonon.meeting.domain.value.Location;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
public class Meeting extends BaseEntity {

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "location")
    private Location location;

    @Column(name = "max_member")
    private int maxMember;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "total_member_count")
    private int totalMemberCount;

    protected Meeting() {
    }

    @Builder
    public Meeting(String title, String description, Location location, int maxMember, String imageUrl, int totalMemberCount) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.maxMember = maxMember;
        this.imageUrl = imageUrl;
        this.totalMemberCount = totalMemberCount;
    }
}
