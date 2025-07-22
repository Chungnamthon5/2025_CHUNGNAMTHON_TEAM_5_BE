package com.chungnamthon.cheonon.user.domain;

import com.chungnamthon.cheonon.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "image", length = 255)
    private String image;

    @Column(name = "nickname", length = 255)
    private String nickname;

    @Column(name = "role", length = 30)
    private String role;
}