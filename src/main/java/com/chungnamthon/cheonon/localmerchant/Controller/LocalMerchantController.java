package com.chungnamthon.cheonon.localmerchant.Controller;

import com.chungnamthon.cheonon.localmerchant.Service.MerchantApiFetchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/merchants")
@RequiredArgsConstructor
public class LocalMerchantController {

    private final MerchantApiFetchService merchantApiFetchService;

    // 최초 데이터 수집 실행용
    @PostMapping("/fetch")
    public ResponseEntity<String> fetchAndSaveMerchants() {
        merchantApiFetchService.fetchAndSaveAll();
        return ResponseEntity.ok("Merchant data successfully fetched and saved.");
    }
}