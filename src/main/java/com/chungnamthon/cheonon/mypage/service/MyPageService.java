package com.chungnamthon.cheonon.mypage.service;

import com.chungnamthon.cheonon.auth.jwt.JwtUtil;
import com.chungnamthon.cheonon.coupon.repository.CouponUserRepository;
import com.chungnamthon.cheonon.mypage.dto.MyPageResponse;
import com.chungnamthon.cheonon.point.repository.PointRepository;
import com.chungnamthon.cheonon.user.domain.User;
import com.chungnamthon.cheonon.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final CouponUserRepository couponUserRepository;
    private final JwtUtil jwtUtil;

    public MyPageResponse getMyPageInfo(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        int currentPoint = pointRepository.sumPointByUserId(userId); // 총 포인트 (ex. +2000 -300 등 계산된 결과)
        int couponCount = couponUserRepository.countByUser_Id(userId); // 유저가 보유한 쿠폰 개수

        return MyPageResponse.builder()
                .userId(user.getId())
                .userName(user.getNickname())
                .profileImageUrl(user.getImage())
                .currentPoint(currentPoint)
                .couponCount(couponCount)
                .build();
    }
}