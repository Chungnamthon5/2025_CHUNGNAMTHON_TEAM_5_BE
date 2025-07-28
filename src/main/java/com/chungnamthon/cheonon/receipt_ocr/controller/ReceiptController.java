package com.chungnamthon.cheonon.receipt_ocr.controller;


import com.chungnamthon.cheonon.global.payload.ResponseDto;
import com.chungnamthon.cheonon.receipt_ocr.dto.ReceiptConfirmResponseDto;
import com.chungnamthon.cheonon.receipt_ocr.dto.ReceiptPreviewResponseDto;
import com.chungnamthon.cheonon.receipt_ocr.dto.ReceiptVerificationRequest;
import com.chungnamthon.cheonon.receipt_ocr.service.ReceiptService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    @PostMapping(value = "/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto<ReceiptPreviewResponseDto> preview(
            @RequestPart("receiptImage") MultipartFile receiptImage,
            @RequestPart("request") String requestJson,
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ReceiptVerificationRequest receiptVerificationRequest = objectMapper.readValue(requestJson, ReceiptVerificationRequest.class);

        ReceiptPreviewResponseDto dto = receiptService.preview(receiptImage, userId, receiptVerificationRequest);
        return ResponseDto.of(dto, "Receipt parsed successfully.");
    }

    @PostMapping("/{previewId}/confirm")
    public ResponseDto<ReceiptConfirmResponseDto> confirm(
            @PathVariable(name = "previewId") Long previewId,
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {
        ReceiptConfirmResponseDto dto = receiptService.confirm(previewId, userId);
        return ResponseDto.of(dto, "Receipt confirmed and 50p awarded!");
    }
}