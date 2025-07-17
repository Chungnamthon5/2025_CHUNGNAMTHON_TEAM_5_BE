package com.chungnamthon.cheonon.users.Dto;

import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsersDto {
    private BigInteger userId;
    private String email;
    private String image;
    private String nickname;
}