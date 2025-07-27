package com.chungnamthon.cheonon.receipt_ocr.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
public class ReceiptConfirmResponseDto {
    private Long          receiptId;
    private Long          userId;
    private Long          merchantId;
    private Long          pointId;
    private LocalDate visitDate;
    private LocalTime visitTime;
    private LocalDateTime createdAt;
    private Integer       currentPoint;
}
