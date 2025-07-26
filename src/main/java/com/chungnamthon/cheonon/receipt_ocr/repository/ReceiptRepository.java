package com.chungnamthon.cheonon.receipt_ocr.repository;

import com.chungnamthon.cheonon.map.domain.Merchant;
import com.chungnamthon.cheonon.receipt_ocr.domain.Receipt;
import com.chungnamthon.cheonon.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    boolean existsByMerchantAndUserAndVisitDateTime(
            Merchant merchant, User user, LocalDateTime visitDateTime);
}
