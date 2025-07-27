package com.chungnamthon.cheonon.receipt_ocr.client;

import com.chungnamthon.cheonon.receipt_ocr.dto.NaverOcrResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.http.client.MultipartBodyBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class NaverOcrClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public NaverOcrClient(
            @Value("${naver.ocr.url}") String baseUrl,
            @Value("${naver.ocr.client-secret}") String clientSecret,
            ObjectMapper objectMapper
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-OCR-SECRET", clientSecret)
                .build();
        this.objectMapper = objectMapper;
    }

    public String parseText(byte[] imageBytes) {
        try {
            // 1) Build message payload for /general
            Map<String,Object> payload = new HashMap<>();
            payload.put("version", "V2");
            payload.put("requestId", UUID.randomUUID().toString());
            payload.put("timestamp", System.currentTimeMillis());
            payload.put("lang", "ko");
            payload.put("enableTableDetection", false);
            List<Map<String,String>> images = List.of(
                    Map.of("format", "png", "name", "receipt")
            );
            payload.put("images", images);
            String messageJson = objectMapper.writeValueAsString(payload);

            // 2) Build multipart body
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("message", messageJson)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");

            ByteArrayResource resource = new ByteArrayResource(imageBytes) {
                @Override public String getFilename() { return "receipt.png"; }
            };
            builder.part("file", resource)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.builder("form-data")
                                    .name("file")
                                    .filename(resource.getFilename())
                                    .build()
                                    .toString()
                    );

            MultiValueMap<String,HttpEntity<?>> parts = builder.build();

            // 3) Execute POST /general
            NaverOcrResponse resp = webClient.post()
                    .uri("/general")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(parts))
                    .retrieve()
                    .bodyToMono(NaverOcrResponse.class)
                    .block();

            if (resp == null || resp.getImages() == null || resp.getImages().isEmpty()) {
                throw new RuntimeException("OCR parsing failed: empty response");
            }

            // 4) Concatenate recognized text with line breaks
            StringBuilder sb = new StringBuilder();
            resp.getImages().forEach(img -> {
                if (img.getFields() != null) {
                    img.getFields().forEach(field ->
                            sb.append(field.getInferText())
                                    .append(System.lineSeparator())
                    );
                }
            });
            return sb.toString().trim();

        } catch (WebClientResponseException e) {
            throw new RuntimeException("OCR service error: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse OCR multipart request", e);
        }
    }
}
