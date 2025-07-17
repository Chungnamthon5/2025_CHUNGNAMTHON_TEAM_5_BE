package com.chungnamthon.cheonon.users.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private BigInteger userId;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "image", length = 255)
    private String image;

    @Column(name = "nickname", length = 255)
    private String nickname;

    @Column(name = "role", length = 30)
    private String role;
}