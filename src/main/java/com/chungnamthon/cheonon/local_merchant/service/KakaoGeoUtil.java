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
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoGeoUtil {

    @Value("${kakao.rest-api-key}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public Optional<double[]> getCoordinates(String address) {
        String url = UriComponentsBuilder
                .fromUriString("https://dapi.kakao.com/v2/local/search/address.json")
                .queryParam("query", address)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
            JsonNode documents = response.getBody().get("documents");

            if (documents.isArray() && documents.size() > 0) {
                JsonNode first = documents.get(0);
                double longitude = first.get("x").asDouble();
                double latitude = first.get("y").asDouble();
                return Optional.of(new double[]{latitude, longitude});
            }

        } catch (Exception e) {
            log.error("주소 좌표 변환 실패: {}", address, e);
        }

        return Optional.empty();
    }
}