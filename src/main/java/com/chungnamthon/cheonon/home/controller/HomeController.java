package com.chungnamthon.cheonon.home.controller;

import com.chungnamthon.cheonon.global.payload.ResponseDto;
import com.chungnamthon.cheonon.home.dto.HomeResponse;
import com.chungnamthon.cheonon.home.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping
    public ResponseDto<HomeResponse> getHomeData() {
        HomeResponse response = homeService.getHomeData();
        return ResponseDto.of(response, "홈 화면 정보 불러오기 성공");
    }
}