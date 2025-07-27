package com.chungnamthon.cheonon.receipt_ocr.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class ReceiptPreviewResponseDto {
    private Long      previewId;
    private Long      merchantId;
    private String    merchantName;
    private LocalDate visitDate;
    private LocalTime visitTime;
    private String    address;
    private Integer   point;
}