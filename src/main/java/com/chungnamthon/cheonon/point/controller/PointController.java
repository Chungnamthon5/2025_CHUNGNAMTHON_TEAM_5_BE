package com.chungnamthon.cheonon.point.controller;

import com.chungnamthon.cheonon.global.payload.ResponseDto;
import com.chungnamthon.cheonon.point.dto.response.PointHistoryResponse;
import com.chungnamthon.cheonon.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @GetMapping
    public ResponseDto<List<PointHistoryResponse>> pointHistory(
            @RequestHeader("Authorization") String token
    ) {
        List<PointHistoryResponse> myPointHistory = pointService.getMyPointHistory(token);
        return ResponseDto.of(myPointHistory, "Successfully retrieved point history.");
    }
}
