package com.chungnamthon.cheonon.Auth.Entity;

import com.chungnamthon.cheonon.users.Entity.UsersEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "Refresh_tokens")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(nullable = false, unique = true, length = 255)
    private String token;

    @Column(name = "created_At",nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expired_At",nullable = false)
    private LocalDateTime expiredAt;

    // 🔽 리프레시 토큰은 하나의 유저에 속한다
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UsersEntity user;
}