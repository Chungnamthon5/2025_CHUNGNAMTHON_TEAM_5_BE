package com.chungnamthon.cheonon.home.controller;

import com.chungnamthon.cheonon.global.payload.ResponseDto;
import com.chungnamthon.cheonon.home.dto.HomeResponse;
import com.chungnamthon.cheonon.home.service.HomeService;
import com.chungnamthon.cheonon.map.dto.AffiliatePreviewResponse;
import com.chungnamthon.cheonon.map.service.AffiliateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;
    private final AffiliateService affiliateService;

    // 🔹 홈 화면 정보 (모임 + 제휴업체 미리보기 등)
    @GetMapping
    public ResponseDto<HomeResponse> getHomeData() {
        HomeResponse response = homeService.getHomeData();
        return ResponseDto.of(response, "홈 화면 정보 불러오기 성공");
    }

    // 🔹 제휴업체 전체 조회 (상세용)
    @GetMapping("/affiliate")
    public ResponseDto<List<AffiliatePreviewResponse>> getAllAffiliates() {
        List<AffiliatePreviewResponse> response = affiliateService.getAllAffiliates();
        return ResponseDto.of(response, "제휴 업체 전체 목록 불러오기 성공");
    }
}