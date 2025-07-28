package com.chungnamthon.cheonon.local_merchant.external;

import com.chungnamthon.cheonon.local_merchant.dto.LocalMerchantDto;
import com.chungnamthon.cheonon.local_merchant.service.KakaoGeoUtil;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalMerchantApiClient {

    private final RestTemplate restTemplate;
    private final KakaoGeoUtil kakaoGeoUtil;

    @Value("${external.merchant-api.signature}")
    private String signature;

    private static final Map<String, String> categoryMap = Map.of(
            "1001", "의류/직물/잡화",
            "1002", "주거생활/내구재",
            "1003", "서비스",
            "1004", "일반/휴게음식/식품",
            "1005", "유통",
            "1006", "자동차/주유",
            "1007", "교육",
            "1008", "의료/미용",
            "1009", "레포츠/문화/취미",
            "1010", "여행"
    );

    public List<LocalMerchantDto> fetchMerchants(String bizType, int pageNum) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id", "34");
        body.put("bizType", bizType);
        body.put("merchantType", "KB");
        body.put("pageNum", pageNum);
        body.put("pageSize", 30);
        body.put("affiliateName", "천안사랑카드");
        body.put("searchKey", "");
        body.put("ptSignature", signature);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", "Academic Research Bot - Cheonan Local Business Analysis");
        headers.set("Accept", "application/json");
        headers.set("Authorization", "Bearer " + signature);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                    "https://search.konacard.co.kr/api/v1/payable-merchants",
                    request,
                    JsonNode.class
            );

            JsonNode root = response.getBody();
            if (root == null || root.get("data") == null || root.get("data").get("merchants") == null) {
                log.warn(" 응답 비정상 (page: {}, bizType: {})", pageNum, bizType);
                return Collections.emptyList();
            }

            JsonNode merchants = root.get("data").get("merchants");
            List<LocalMerchantDto> result = new ArrayList<>();

            for (JsonNode node : merchants) {
                String address = node.get("addr").asText();
                Optional<double[]> coords = kakaoGeoUtil.getCoordinates(address);

                Double latitude = null;
                Double longitude = null;

                if (coords.isPresent()) {
                    latitude = coords.get()[0];
                    longitude = coords.get()[1];
                }

                result.add(LocalMerchantDto.builder()
                        .merchantSeq(String.valueOf(BigInteger.valueOf(node.get("seq").asLong())))
                        .name(node.get("simpleNm").asText())
                        .category(categoryMap.getOrDefault(bizType, "기타"))
                        .address(address)
                        .tel(node.has("telNo") ? node.get("telNo").asText() : "")
                        .latitude(latitude)
                        .longitude(longitude)
                        .build());
            }

            log.info("{}개 수집 완료 (bizType: {}, page: {})", result.size(), bizType, pageNum);
            return result;

        } catch (Exception e) {
            log.error("API fetch error (bizType: {}, page: {}): {}", bizType, pageNum, e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}