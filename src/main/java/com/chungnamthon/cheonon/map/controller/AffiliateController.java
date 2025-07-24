package com.chungnamthon.cheonon.map.controller;

import com.chungnamthon.cheonon.global.payload.ResponseDto;
import com.chungnamthon.cheonon.map.dto.AffiliateDto;
import com.chungnamthon.cheonon.map.service.AffiliateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/affiliates")
@RequiredArgsConstructor
public class AffiliateController {

    private final AffiliateService affiliateService;

    @GetMapping
    public ResponseDto<Page<AffiliateDto>> list(Pageable pageable) {
        Page<AffiliateDto> data = affiliateService.list(pageable);
        return ResponseDto.of(data, "Affiliate list fetched successfully.");
    }

    @GetMapping("/{id}")
    public ResponseDto<AffiliateDto> detail(@PathVariable Long id) {
        AffiliateDto dto = affiliateService.detail(id);
        return ResponseDto.of(dto, "Affiliate detail fetched successfully.");
    }
}

