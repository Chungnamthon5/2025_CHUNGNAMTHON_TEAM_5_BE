package com.chungnamthon.cheonon.local_merchant.service;

import com.chungnamthon.cheonon.local_merchant.external.ExternalMerchantApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantApiFetchService {

    private final ExternalMerchantApiClient apiClient;
    private final MerchantService merchantService;

    private static final List<String> BIZ_TYPE_LIST = List.of(
            "1001", "1002", "1003", "1004", "1005",
            "1006", "1007", "1008", "1009", "1010"
    );

    private static final int MAX_PAGE = 353;

    public void fetchAndSaveAll() {
        log.info("가맹점 데이터 수집 시작 (단일스레드 모드)");

        int totalProcessed = 0;
        int totalSaved = 0;
        int totalSkipped = 0;

        for (String bizType : BIZ_TYPE_LIST) {
            log.info("카테고리 처리 시작: {} ({})", bizType, getCategoryName(bizType));

            int categoryProcessed = 0;

            for (int page = 1; page <= MAX_PAGE; page++) {
                try {
                    var dtoList = apiClient.fetchMerchants(bizType, page);

                    if (dtoList.isEmpty()) {
                        log.info("데이터 없음: bizType={}, page={} - 카테고리 완료", bizType, page);
                        break;
                    }

                    for (var dto : dtoList) {
                        try {
                            // 저장 시도 (중복이면 자동 스킵 또는 좌표 업데이트)
                            merchantService.saveMerchant(dto);
                            totalSaved++;

                        } catch (Exception e) {
                            log.warn("저장 스킵/실패: {} - {}", dto.getName(), e.getMessage());
                            totalSkipped++;
                        }
                    }

                    categoryProcessed += dtoList.size();
                    totalProcessed += dtoList.size();

                    // 100페이지마다 진행상황 로그
                    if (page % 100 == 0) {
                        log.info("진행률: bizType={}, page={}, 카테고리누적={}",
                                bizType, page, categoryProcessed);
                    }

                    // API 호출 간격 (부하 방지)
                    Thread.sleep(50);

                } catch (Exception e) {
                    log.error("페이지 처리 실패: bizType={}, page={} - {}",
                            bizType, page, e.getMessage());
                }
            }

            log.info("카테고리 완료: {} - 처리={}", bizType, categoryProcessed);
        }

        log.info("전체 수집 완료 총처리: {}, 신규저장: {}, 스킵: {}",
                totalProcessed, totalSaved, totalSkipped);
    }

    private String getCategoryName(String bizType) {
        return switch (bizType) {
            case "1001" -> "의류/직물/잡화";
            case "1002" -> "주거생활/내구재";
            case "1003" -> "서비스";
            case "1004" -> "일반/휴게음식/식품";
            case "1005" -> "유통";
            case "1006" -> "자동차/주유";
            case "1007" -> "교육";
            case "1008" -> "의료/미용";
            case "1009" -> "레포츠/문화/취미";
            case "1010" -> "여행";
            default -> "기타";
        };
    }
}
