package com.chungnamthon.cheonon.poweruser.domain;

import com.chungnamthon.cheonon.global.domain.BaseEntity;
import com.chungnamthon.cheonon.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "power_user")
public class PowerUser extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "total_point")
    private Integer totalPoint;

    @Column(name = "ranking")
    private Integer ranking;

    @Column(name = "week_of")
    private LocalDate weekOf; // 예: 2025-07-22 기준이면 7월 22일 포함된 주의 월요일

    @Builder
    public PowerUser(User user, Integer totalPoint, Integer ranking, LocalDate weekOf) {
        this.user = user;
        this.totalPoint = totalPoint;
        this.ranking = ranking;
        this.weekOf = weekOf;
    }

    public void setRank(int rank) {
        this.ranking = ranking;
    }

    protected PowerUser() {
    }
}