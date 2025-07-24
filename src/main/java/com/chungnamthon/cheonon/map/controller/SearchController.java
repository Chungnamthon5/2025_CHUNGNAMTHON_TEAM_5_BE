package com.chungnamthon.cheonon.map.controller;

import com.chungnamthon.cheonon.global.payload.ResponseDto;
import com.chungnamthon.cheonon.map.dto.SearchResultDto;
import com.chungnamthon.cheonon.map.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseDto<Page<SearchResultDto>> search(
            @RequestParam String keyword,
            Pageable pageable
    ) {
        Page<SearchResultDto> data = searchService.searchByName(keyword, pageable);
        return ResponseDto.of(data, "Search by name succeeded.");
    }
}

