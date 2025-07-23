package com.chungnamthon.cheonon.local_merchant.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpHeaders;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoGeoUtil {

    @Value("${kakao.rest-api-key}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // ✅ 주소 캐시 맵 추가 (동일 주소 재요청 방지)
    private final Map<String, double[]> geoCache = new ConcurrentHashMap<>();

    public Optional<double[]> getCoordinates(String address) {
        if (address == null || address.trim().isEmpty()) {
            log.warn("❌ 주소가 비어있음");
            return Optional.empty();
        }

        // ✅ 캐시된 좌표가 있으면 바로 반환
        if (geoCache.containsKey(address)) {
            return Optional.of(geoCache.get(address));
        }

        try {
            URI uri = UriComponentsBuilder
                    .fromUriString("https://dapi.kakao.com/v2/local/search/address.json")
                    .queryParam("query", address.trim())
                    .build()
                    .encode()
                    .toUri();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoApiKey);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(uri, HttpMethod.GET, entity, JsonNode.class);

            JsonNode documents = response.getBody().get("documents");

            if (documents != null && documents.isArray() && documents.size() > 0) {
                JsonNode first = documents.get(0);
                double lon = first.get("x").asDouble();
                double lat = first.get("y").asDouble();

                double[] coords = new double[]{lat, lon};

                // ✅ 캐시에 저장
                geoCache.put(address, coords);

                return Optional.of(coords);
            } else {
                log.warn("❌ 카카오 결과 없음: {}", address);
            }

        } catch (Exception e) {
            log.error("💥 카카오 주소 변환 실패: {} - {}", address, e.getMessage());
        }

        return Optional.empty();
    }
}
