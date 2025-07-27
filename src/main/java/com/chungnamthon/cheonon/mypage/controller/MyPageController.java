package com.chungnamthon.cheonon.mypage.controller;

import com.chungnamthon.cheonon.global.payload.ResponseDto;
import com.chungnamthon.cheonon.mypage.dto.MyPageResponse;
import com.chungnamthon.cheonon.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping
    public ResponseDto<MyPageResponse> getMyPage(
            @RequestHeader("Authorization") String token
    ) {
        MyPageResponse response = myPageService.getMyPageInfo(token);
        return ResponseDto.of(response, "마이페이지 정보 조회 성공");
    }
}