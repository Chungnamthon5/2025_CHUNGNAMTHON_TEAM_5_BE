package com.chungnamthon.cheonon.receipt_ocr.repository;

import com.chungnamthon.cheonon.local_merchant.domain.Merchant;
import com.chungnamthon.cheonon.receipt_ocr.domain.Receipt;
import com.chungnamthon.cheonon.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    boolean existsByMerchantAndUserAndVisitDateAndVisitTime(Merchant merchant, User user, LocalDate visitDate, LocalTime visitTime);
}
