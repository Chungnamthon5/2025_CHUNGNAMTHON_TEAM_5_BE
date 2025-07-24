package com.chungnamthon.cheonon.map.controller;

import com.chungnamthon.cheonon.global.payload.ResponseDto;
import com.chungnamthon.cheonon.map.dto.MerchantDto;
import com.chungnamthon.cheonon.map.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @GetMapping
    public ResponseDto<Page<MerchantDto>> list(Pageable pageable) {
        Page<MerchantDto> data = merchantService.list(pageable);
        return ResponseDto.of(data, "Merchant list fetched successfully.");
    }

    @GetMapping("/{id}")
    public ResponseDto<MerchantDto> detail(@PathVariable Long id) {
        MerchantDto dto = merchantService.detail(id);
        return ResponseDto.of(dto, "Merchant detail fetched successfully.");
    }
}

