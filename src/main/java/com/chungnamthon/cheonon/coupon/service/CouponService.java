package com.chungnamthon.cheonon.coupon.service;

import com.chungnamthon.cheonon.auth.jwt.JwtUtil;
import com.chungnamthon.cheonon.coupon.domain.Coupon;
import com.chungnamthon.cheonon.coupon.domain.CouponUser;
import com.chungnamthon.cheonon.coupon.dto.request.ExchangeCouponRequest;
import com.chungnamthon.cheonon.coupon.dto.response.CouponListResponse;
import com.chungnamthon.cheonon.coupon.dto.response.ExchangeCouponResponse;
import com.chungnamthon.cheonon.coupon.dto.response.MyCouponListResponse;
import com.chungnamthon.cheonon.coupon.repository.CouponRepository;
import com.chungnamthon.cheonon.coupon.repository.CouponUserRepository;
import com.chungnamthon.cheonon.global.exception.BusinessException;
import com.chungnamthon.cheonon.global.exception.error.AuthenticationError;
import com.chungnamthon.cheonon.global.exception.error.CouponError;
import com.chungnamthon.cheonon.global.exception.error.PointError;
import com.chungnamthon.cheonon.point.domain.Point;
import com.chungnamthon.cheonon.point.domain.value.PaymentType;
import com.chungnamthon.cheonon.point.repository.PointRepository;
import com.chungnamthon.cheonon.user.domain.User;
import com.chungnamthon.cheonon.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponUserRepository couponUserRepository;
    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public ExchangeCouponResponse exchangeCoupon(String token, ExchangeCouponRequest exchangeCouponRequest) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(AuthenticationError.USER_NOT_FOUND));

        // 쿠폰 존재 여부 확인
        Long couponId = exchangeCouponRequest.couponId();
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(CouponError.COUPON_NOT_FOUND));

        // 이미 발급받은 쿠폰 예외 처리
        if (couponUserRepository.existsByUserIdAndCouponId(userId, couponId)) {
            throw new BusinessException(CouponError.COUPON_ALREADY_ISSUED);
        }

        Integer couponPoint = coupon.getPoint(); // 쿠폰 금액

        // 사용자의 가장 최근 포인트 내역 추출
        Point point = pointRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new BusinessException(PointError.POINT_NOT_FOUND));

        Integer currentPoint = point.getCurrentPoint(); // 사용자의 현재 보유 포인트

        if (currentPoint >= couponPoint) {
            String title = coupon.getTitle();
            LocalDate expirationDate = coupon.getExpirationDate();

            // 발급받은 쿠폰 저장
            CouponUser couponUser = CouponUser.builder()
                    .coupon(coupon)
                    .user(user)
                    .expirationDate(expirationDate)
                    .isUsed(false)
                    .build();

            couponUserRepository.save(couponUser);

            // 사용된 포인트만큼 차감하여 내역 추가
            Point exchangePoint = Point.builder()
                    .user(user)
                    .paymentType(PaymentType.EXCHANGE_COUPON)
                    .changedPoint(-couponPoint)
                    .currentPoint(currentPoint - couponPoint)
                    .build();

            pointRepository.save(exchangePoint);

            return new ExchangeCouponResponse(couponId, title, couponPoint, expirationDate);
        } else { // 포인트 부족 예외 처리
            throw new BusinessException(PointError.INSUFFICIENT_POINT);
        }
    }

    public List<CouponListResponse> getCouponList() {
        List<Coupon> couponList = couponRepository.findAll();

        List<CouponListResponse> couponListResponses = new ArrayList<>();
        for (Coupon coupon : couponList) {
            Long id = coupon.getId();
            String title = coupon.getTitle();
            Integer point = coupon.getPoint();
            Integer expirationPeriod = coupon.getExpirationPeriod();
            String imageUrl = coupon.getImageUrl();

            CouponListResponse couponListResponse
                    = new CouponListResponse(id, title, point, expirationPeriod, imageUrl);
            couponListResponses.add(couponListResponse);
        }

        return couponListResponses;
    }

    public List<MyCouponListResponse> getMyCouponList(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        List<CouponUser> myCoupons = couponUserRepository.findByUserId(userId);

        List<Coupon> coupons = new ArrayList<>();
        for (CouponUser myCoupon : myCoupons) {
            Long couponId = myCoupon.getCoupon().getId();
            Coupon coupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> new BusinessException(CouponError.COUPON_NOT_FOUND));
            coupons.add(coupon);
        }

        List<MyCouponListResponse> myCouponListResponses = new ArrayList<>();
        for (Coupon myCoupon : coupons) {
            Long id = myCoupon.getId(); // 쿠폰의 id (내 쿠폰 id x)
            String title = myCoupon.getTitle();
            Integer point = myCoupon.getPoint();
            LocalDate expirationPeriod = myCoupon.getCreatedAt().toLocalDate().plusDays(myCoupon.getExpirationPeriod());
            String imageUrl = myCoupon.getImageUrl();

            MyCouponListResponse myCouponListResponse
                    = new MyCouponListResponse(id, userId, title, point, expirationPeriod, imageUrl);

            myCouponListResponses.add(myCouponListResponse);
        }

        return myCouponListResponses;
    }
}
