package com.chungnamthon.cheonon.local_merchant.service;

import com.chungnamthon.cheonon.local_merchant.external.ExternalMerchantApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantApiFetchService {

    private final ExternalMerchantApiClient apiClient;
    private final LocalMerchantService localMerchantService;

    private static final List<String> BIZ_TYPE_LIST = List.of(
            "1001", "1002", "1003", "1004", "1005",
            "1006", "1007", "1008", "1009", "1010"
    );

    private static final int MAX_PAGE = 353; // 최대 페이지 수 (넉넉하게 설정)
    private static final int THREAD_COUNT = 10; // 병렬 처리용 스레드 수

    public void fetchAndSaveAll() {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        for (String bizType : BIZ_TYPE_LIST) {
            for (int page = 1; page <= MAX_PAGE; page++) {
                final String currentBizType = bizType;
                final int currentPage = page;

                executor.submit(() -> {
                    try {
                        var dtoList = apiClient.fetchMerchants(currentBizType, currentPage);

                        if (dtoList.isEmpty()) {
                            log.info("📦 데이터 없음: bizType={}, page={}", currentBizType, currentPage);
                            return;
                        }

                        dtoList.forEach(localMerchantService::saveMerchant);
                        log.info("✅ 저장 완료: bizType={}, page={}, count={}",
                                currentBizType, currentPage, dtoList.size());

                    } catch (Exception e) {
                        log.error("💥 예외 발생: bizType={}, page={}, msg={}",
                                currentBizType, currentPage, e.getMessage(), e);
                    }
                });
            }
        }

        executor.shutdown();
    }
}