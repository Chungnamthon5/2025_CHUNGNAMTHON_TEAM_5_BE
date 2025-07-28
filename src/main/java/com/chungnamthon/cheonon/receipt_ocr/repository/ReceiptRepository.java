package com.chungnamthon.cheonon.receipt_ocr.repository;

import com.chungnamthon.cheonon.local_merchant.domain.Merchant;
import com.chungnamthon.cheonon.receipt_ocr.domain.Receipt;
import com.chungnamthon.cheonon.receipt_ocr.domain.ReceiptPreview;
import com.chungnamthon.cheonon.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    // 새로 추가할 메서드: 30분 이내 같은 가맹점, 같은 방문시간 영수증 조회
    @Query("SELECT r FROM Receipt r WHERE r.merchant = :merchant " +
            "AND r.visitDate = :visitDate " +
            "AND r.visitTime = :visitTime " +
            "AND r.createdAt > :createdAfter " +
            "ORDER BY r.createdAt ASC")
    List<Receipt> findByMerchantAndVisitDateAndVisitTimeAndCreatedAtAfter(
            @Param("merchant") Merchant merchant,
            @Param("visitDate") LocalDate visitDate,
            @Param("visitTime") LocalTime visitTime,
            @Param("createdAfter") LocalDateTime createdAfter
    );

    boolean existsByPreview(ReceiptPreview preview);

    List<Receipt> findByMerchantAndCreatedAtAfter(Merchant merchant, LocalDateTime thirtyMinutesAgo);
}
