package com.chungnamthon.cheonon.receipt_ocr.service;

import com.chungnamthon.cheonon.local_merchant.domain.Merchant;
import com.chungnamthon.cheonon.local_merchant.repository.MerchantRepository;
import com.chungnamthon.cheonon.point.domain.Point;
import com.chungnamthon.cheonon.point.domain.value.PaymentType;
import com.chungnamthon.cheonon.point.repository.PointRepository;
import com.chungnamthon.cheonon.receipt_ocr.client.NaverOcrClient;
import com.chungnamthon.cheonon.receipt_ocr.domain.Receipt;
import com.chungnamthon.cheonon.receipt_ocr.domain.ReceiptPreview;
import com.chungnamthon.cheonon.receipt_ocr.dto.ReceiptConfirmResponseDto;
import com.chungnamthon.cheonon.receipt_ocr.dto.ReceiptPreviewResponseDto;
import com.chungnamthon.cheonon.receipt_ocr.repository.ReceiptPreviewRepository;
import com.chungnamthon.cheonon.receipt_ocr.repository.ReceiptRepository;
import com.chungnamthon.cheonon.user.domain.User;
import com.chungnamthon.cheonon.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{4}[.\\-/]\\d{1,2}[.\\-/]\\d{1,2})");
    // 초가 반드시 포함된 시:분:초 형태만 매칭
    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d{1,2}:\\d{2}:\\d{2})");
    // 코나 결제 키워드 목록
    private static final List<String> REQUIRED_CARD_KEYWORDS = List.of("코나", "코나카드", "KONAI", "KONA");

    public ReceiptPreviewResponseDto preview(MultipartFile file, Long userId) throws Exception {
        String text = ocrClient.parseText(file.getBytes());

        // 1) Cheonan 주소 포함 여부 검증
        if (!text.contains("천안")) {
            throw new IllegalArgumentException("Receipt must be for a store in Cheonan.");
        }
        // 2) Kona 결제 여부 검증 (키워드 중 하나만 포함돼도 OK)
        boolean hasKona = REQUIRED_CARD_KEYWORDS.stream()
                .anyMatch(text::contains);
        if (!hasKona) {
            throw new IllegalArgumentException("영수증에 코나 결제 내역이 없습니다.");
        }

        // 3) OCR 텍스트 전체에서 숫자 추출 후, 가맹점 ID와 매칭
        Pattern numberPattern = Pattern.compile("\\d+");
        Matcher numberMatcher = numberPattern.matcher(text);
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
            throw new EntityNotFoundException("Merchant not found in OCR text.");
        }

        // 4) 방문 일시 파싱
        LocalDate date = LocalDate.now();
        Matcher mDate = DATE_PATTERN.matcher(text);
        if (mDate.find()) {
            date = LocalDate.parse(mDate.group(1)
                    .replace('.', '-')
                    .replace('/', '-'));
        }
        // 시:분:초 형식만 반영, 없으면 현재 시각 유지
        LocalTime time = LocalTime.now();
        Matcher mTime = TIME_PATTERN.matcher(text);
        if (mTime.find()) {
            time = LocalTime.parse(mTime.group(1));
        }
        // Todo 수정 ㅎㅎ
        /*LocalDate visitDate = LocalDate.of(date);
        LocalTime visitTime = LocalTime.of(time);*/

        // 5) User 검증
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        // 6) ReceiptPreview 저장
        ReceiptPreview preview = new ReceiptPreview();
        preview.setUser(user);
        preview.setMerchant(merchant);
        // Todo 수정 ㅎㅎ
        /*preview.setVisitDate(visitDate);
        preview.setVisitTime(visitTime);*/
        preview = previewRepo.save(preview);

        // 7) DTO 반환
        return ReceiptPreviewResponseDto.builder()
                .previewId(preview.getId())
                .merchantId(merchant.getId())
                .merchantName(merchant.getName())
                .visitDate(date)
                .visitTime(time)
                .address(merchant.getAddress())
                .point(300)
                .build();
    }

    public ReceiptConfirmResponseDto confirm(Long previewId, Long userId) {
        ReceiptPreview preview = previewRepo.findById(previewId)
                .orElseThrow(() -> new EntityNotFoundException("No pending receipt: " + previewId));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        if (receiptRepo.existsByMerchantAndUserAndVisitDateAndVisitTime(
                (preview.getMerchant()), user, preview.getVisitDate(), preview.getVisitTime())) {
            throw new IllegalStateException("Receipt already confirmed for this visit.");
        }

        int previous = pointRepo.findFirstByUserOrderByCreatedAtDesc(user)
                .map(Point::getCurrentPoint)
                .orElse(0);
        int changed = 300;
        int current = previous + changed;

        Point p = Point.builder()
                .user(user)
                .paymentType(PaymentType.PAYMENT_VERIFICATION)
                .changedPoint(changed)
                .currentPoint(current)
                .build();
        p = pointRepo.save(p);

        Receipt receipt = new Receipt();
        receipt.setPreview(preview);
        receipt.setUser(user);
        receipt.setMerchant(preview.getMerchant());
        receipt.setPoint(p);
        receipt.setVisitDate(preview.getVisitDate());
        receipt.setVisitTime(preview.getVisitTime());
        receipt = receiptRepo.save(receipt);

        return ReceiptConfirmResponseDto.builder()
                .receiptId(receipt.getId())
                .userId(user.getId())
                .merchantId(preview.getMerchant().getId())
                .pointId(p.getId())
                .visitDate(preview.getVisitDate())
                .visitTime(preview.getVisitTime())
                .createdAt(receipt.getCreatedAt())
                .currentPoint(current)
                .build();
    }
}
