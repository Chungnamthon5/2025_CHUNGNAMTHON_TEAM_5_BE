package com.chungnamthon.cheonon.mypage.service;

import com.chungnamthon.cheonon.auth.jwt.JwtUtil;
import com.chungnamthon.cheonon.coupon.repository.CouponUserRepository;
import com.chungnamthon.cheonon.global.exception.BusinessException;
import com.chungnamthon.cheonon.global.exception.error.AuthenticationError;
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
        Long userId;

        try {
            userId = jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            throw new BusinessException(AuthenticationError.INVALID_TOKEN);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(AuthenticationError.USER_NOT_FOUND));

        int currentPoint = pointRepository.sumPointByUserId(userId);
        int couponCount = couponUserRepository.countByUser_Id(userId);

        return MyPageResponse.builder()
                .userId(user.getId())
                .userName(user.getNickname())
                .profileImageUrl(user.getImage())
                .currentPoint(currentPoint)
                .couponCount(couponCount)
                .build();
    }
}