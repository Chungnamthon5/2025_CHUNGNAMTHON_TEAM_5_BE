package com.chungnamthon.cheonon.receipt_ocr.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReceiptConfirmResponseDto {
    private Long          receiptId;
    private Long          userId;
    private Long          merchantId;
    private Long          pointId;
    private LocalDateTime visitDateTime;
    private LocalDateTime createdAt;
    private Integer       currentPoint;
}
