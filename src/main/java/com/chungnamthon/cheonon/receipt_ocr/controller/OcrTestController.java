package com.chungnamthon.cheonon.receipt_ocr.controller;

import com.chungnamthon.cheonon.receipt_ocr.client.NaverOcrClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ocr")
@RequiredArgsConstructor
public class OcrTestController {
    private final NaverOcrClient ocrClient;

    /**
     * 업로드된 이미지에서 OCR로 추출한 원본 텍스트만 반환
     * 예) HTTP 200
     *   "천안시 코나카드 12,000원\n2025-07-26 14:32\n..."
     */
    @PostMapping("/extract")
    public ResponseEntity<String> extractText(@RequestPart("file") MultipartFile file) throws Exception {
        String parsed = ocrClient.parseText(file.getBytes());
        return ResponseEntity.ok(parsed);
    }
}

