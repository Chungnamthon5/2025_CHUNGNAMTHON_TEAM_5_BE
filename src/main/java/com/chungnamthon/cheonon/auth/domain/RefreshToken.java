package com.chungnamthon.cheonon.auth.domain;

import com.chungnamthon.cheonon.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String token;

    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expired_at",nullable = false)
    private LocalDateTime expiredAt;

    //리프레시 토큰은 하나의 유저에 속한다
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void update(String newToken, LocalDateTime createdAt, LocalDateTime expiredAt) {
        this.token = newToken;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
    }
}