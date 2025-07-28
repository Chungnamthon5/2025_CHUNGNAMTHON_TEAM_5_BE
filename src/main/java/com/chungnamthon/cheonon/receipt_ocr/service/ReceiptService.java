package com.chungnamthon.cheonon.receipt_ocr.service;

import com.chungnamthon.cheonon.global.exception.BusinessException;
import com.chungnamthon.cheonon.global.exception.error.AuthenticationError;
import com.chungnamthon.cheonon.global.exception.error.ReceiptError;
import com.chungnamthon.cheonon.local_merchant.domain.Merchant;
import com.chungnamthon.cheonon.local_merchant.repository.MerchantRepository;
import com.chungnamthon.cheonon.point.domain.Point;
import com.chungnamthon.cheonon.point.domain.value.PaymentType;
import com.chungnamthon.cheonon.point.repository.PointRepository;
import com.chungnamthon.cheonon.point.service.PointService;
import com.chungnamthon.cheonon.receipt_ocr.client.NaverOcrClient;
import com.chungnamthon.cheonon.receipt_ocr.domain.Receipt;
import com.chungnamthon.cheonon.receipt_ocr.domain.ReceiptPreview;
import com.chungnamthon.cheonon.receipt_ocr.dto.ReceiptConfirmResponseDto;
import com.chungnamthon.cheonon.receipt_ocr.dto.ReceiptPreviewResponseDto;
import com.chungnamthon.cheonon.receipt_ocr.dto.ReceiptVerificationRequest;
import com.chungnamthon.cheonon.receipt_ocr.repository.ReceiptPreviewRepository;
import com.chungnamthon.cheonon.receipt_ocr.repository.ReceiptRepository;
import com.chungnamthon.cheonon.user.domain.User;
import com.chungnamthon.cheonon.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReceiptService {

    private final NaverOcrClient ocrClient;
    private final MerchantRepository merchantRepo;
    private final UserRepository userRepo;
    private final ReceiptPreviewRepository previewRepo;
    private final ReceiptRepository receiptRepo;
    private final PointRepository pointRepo;
    private final PointService pointService;

    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{4}[.\\-/]\\d{1,2}[.\\-/]\\d{1,2})");
    private static final Pattern TIME_PATTERN = Pattern.compile("\\b(\\d{1,2}):\\s*(\\d{2})(?::\\s*(\\d{2}))?\\b");
    private static final List<String> REQUIRED_CARD_KEYWORDS = List.of("코나", "코나카드", "KONAI", "KONA");
    private static final double VERIFICATION_RADIUS_METERS = 100.0;

    public ReceiptPreviewResponseDto preview(MultipartFile file, Long userId, ReceiptVerificationRequest receiptVerificationRequest) throws Exception {
        String text = ocrClient.parseText(file.getBytes());
        String flattenedText = text.replaceAll("[\\s\\n]+", " ");

        if (!flattenedText.contains("천안")) {
            throw new BusinessException(ReceiptError.INVALID_CITY);
        }
        boolean hasKona = REQUIRED_CARD_KEYWORDS.stream().anyMatch(flattenedText::contains);
        if (!hasKona) {
            throw new BusinessException(ReceiptError.INVALID_CARD_TYPE);
        }

        Pattern numberPattern = Pattern.compile("\\d+");
        Matcher numberMatcher = numberPattern.matcher(flattenedText);
        Merchant merchant = null;
        while (numberMatcher.find()) {
            String potentialId = numberMatcher.group();
            Optional<Merchant> opt = merchantRepo.findByMerchantSeq(potentialId);
            if (opt.isPresent()) {
                merchant = opt.get();
                break;
            }
        }
        if (merchant == null) {
            throw new BusinessException(ReceiptError.MERCHANT_NOT_FOUND);
        }

        if (receiptVerificationRequest.latitude() == null || receiptVerificationRequest.longitude() == null)
            throw new BusinessException(ReceiptError.USER_LOCATION_REQUIRED);
        if (merchant.getLatitude() == null || merchant.getLongitude() == null)
            throw new BusinessException(ReceiptError.MERCHANT_LOCATION_MISSING);

        double distance = calculateDistance(
                receiptVerificationRequest.latitude(), receiptVerificationRequest.longitude(),
                merchant.getLatitude(), merchant.getLongitude()
        );
        if (distance > VERIFICATION_RADIUS_METERS) {
            throw new BusinessException(ReceiptError.TOO_FAR_FROM_MERCHANT);
        }

        LocalDate date = LocalDate.now();
        Matcher mDate = DATE_PATTERN.matcher(flattenedText);
        if (mDate.find()) {
            date = LocalDate.parse(mDate.group(1).replace('.', '-').replace('/', '-'));
        }

        LocalTime time;
        Matcher mTime = TIME_PATTERN.matcher(flattenedText);
        if (mTime.find()) {
            try {
                int hour = Integer.parseInt(mTime.group(1));
                int minute = Integer.parseInt(mTime.group(2));
                int second = mTime.group(3) != null ? Integer.parseInt(mTime.group(3)) : 0;
                if (hour < 0 || hour > 23) {
                    throw new BusinessException(ReceiptError.INVALID_HOUR);
                }
                if (minute < 0 || minute > 59) {
                    throw new BusinessException(ReceiptError.INVALID_MINUTE);
                }
                if (second < 0 || second > 59) {
                    throw new BusinessException(ReceiptError.INVALID_SECOND);
                }
                time = LocalTime.of(hour, minute, second);
                log.info("방문 시각 추출 완료: {}", time);
            } catch (NumberFormatException e) {
                throw new BusinessException(ReceiptError.INVALID_TIME_FORMAT);
            }
        } else {
            throw new BusinessException(ReceiptError.TIME_NOT_FOUND);
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new BusinessException(AuthenticationError.USER_NOT_FOUND));

        ReceiptPreview receiptPreview = ReceiptPreview.builder()
                .user(user)
                .merchant(merchant)
                .visitDate(date)
                .visitTime(time)
                .userLatitude(receiptVerificationRequest.latitude())
                .userLongitude(receiptVerificationRequest.longitude())
                .build();

        receiptPreview = previewRepo.save(receiptPreview);

        return ReceiptPreviewResponseDto.builder()
                .previewId(receiptPreview.getId())
                .merchantId(merchant.getId())
                .merchantName(merchant.getName())
                .visitDate(date)
                .visitTime(time)
                .address(merchant.getAddress())
                .build();
    }

    public ReceiptConfirmResponseDto confirm(Long previewId, Long userId) {
        ReceiptPreview preview = previewRepo.findById(previewId)
                .orElseThrow(() -> new BusinessException(ReceiptError.PREVIEW_NOT_FOUND));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new BusinessException(AuthenticationError.USER_NOT_FOUND));

        if (receiptRepo.existsByPreview(preview)) {
            throw new BusinessException(ReceiptError.RECEIPT_ALREADY_CONFIRMED);
        }

        Receipt receipt = Receipt.builder()
                .preview(preview)
                .user(user)
                .merchant(preview.getMerchant())
                .visitDate(preview.getVisitDate())
                .visitTime(preview.getVisitTime())
                .build();
        receipt = receiptRepo.save(receipt);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyMinutesAgo = now.minusMinutes(30);

        List<Receipt> recentReceipts = receiptRepo.findByMerchantAndCreatedAtAfter(
                preview.getMerchant(), thirtyMinutesAgo);

        log.info("30분 이내 같은 가맹점 인증 완료자 수: {}", recentReceipts.size());

        int finalCurrentPoint = pointRepo.findTopByUserIdOrderByCreatedAtDesc(userId)
                .map(Point::getCurrentPoint).orElse(0);

        if (recentReceipts.size() >= 2) {
            log.info("인증 완료. 포인트 지급 대상자:");

            for (Receipt recentReceipt : recentReceipts) {
                try {
                    boolean alreadyRewarded = hasAlreadyReceivedTeamBonus(recentReceipt.getUser().getId(),
                            recentReceipt.getMerchant().getId(), recentReceipt.getCreatedAt());

                    if (!alreadyRewarded) {
                        pointService.rewardForReceiptVerification(
                                recentReceipt.getUser().getId(),
                                recentReceipt.getMerchant().getIsAffiliate()
                        );
                        log.info("사용자 {}에게 팀 인증 포인트 지급 완료", recentReceipt.getUser().getId());
                    } else {
                        log.info("사용자 {}는 이미 포인트를 받았습니다", recentReceipt.getUser().getId());
                    }
                } catch (Exception e) {
                    log.error("사용자 {}에게 포인트 지급 실패: {}", recentReceipt.getUser().getId(), e.getMessage());
                }
            }

            finalCurrentPoint = pointRepo.findTopByUserIdOrderByCreatedAtDesc(userId)
                    .map(Point::getCurrentPoint).orElse(finalCurrentPoint);
        } else {
            log.info("추가 인증 대기 중. 현재 인증자 수: {}/2", recentReceipts.size());
        }

        return ReceiptConfirmResponseDto.builder()
                .receiptId(receipt.getId())
                .userId(user.getId())
                .merchantId(preview.getMerchant().getId())
                .visitDate(preview.getVisitDate())
                .visitTime(preview.getVisitTime())
                .createdAt(receipt.getCreatedAt())
                .currentPoint(finalCurrentPoint)
                .build();
    }

    // 특정 시간대에 이미 팀 보너스를 받았는지 확인
    private boolean hasAlreadyReceivedTeamBonus(Long userId, Long merchantId, LocalDateTime receiptCreatedAt) {
        LocalDateTime checkStart = receiptCreatedAt.minusMinutes(30);
        LocalDateTime checkEnd = receiptCreatedAt.plusMinutes(30);

        return pointRepo.existsByUserIdAndPaymentTypeAndCreatedAtBetween(
                userId, PaymentType.PAYMENT_VERIFICATION, checkStart, checkEnd
        ) || pointRepo.existsByUserIdAndPaymentTypeAndCreatedAtBetween(
                userId, PaymentType.PARTNER_STORE_BONUS, checkStart, checkEnd
        );
    }

    // 거리 계산
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
}