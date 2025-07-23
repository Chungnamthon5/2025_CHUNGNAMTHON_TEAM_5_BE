package com.chungnamthon.cheonon.local_merchant.service;

import com.chungnamthon.cheonon.local_merchant.dto.LocalMerchantDto;
import com.chungnamthon.cheonon.local_merchant.domain.LocalMerchant;
import com.chungnamthon.cheonon.local_merchant.repository.LocalMerchantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j  // 🆕 로깅을 위해 추가
@Service
@RequiredArgsConstructor
public class LocalMerchantService {

    private final LocalMerchantRepository localMerchantRepository;
    private final KakaoGeoUtil kakaoGeoUtil;

    // 🆕 좌표 업데이트 모드 설정
    @Value("${merchant.update-coordinates:false}")
    private boolean updateCoordinatesMode;

    public void saveMerchant(LocalMerchantDto dto) {
        Optional<LocalMerchant> existing = localMerchantRepository.findByMerchantSeq(dto.getMerchantSeq());

        if (existing.isPresent()) {
            // 🆕 업데이트 모드가 활성화된 경우 좌표 업데이트 시도
            if (updateCoordinatesMode) {
                LocalMerchant merchant = existing.get();
                if (needsCoordinateUpdate(merchant)) {
                    updateMerchantCoordinates(merchant, dto.getAddress());
                }
            }
            return; // 이미 저장된 merchant_seq는 처리 완료
        }

        // 좌표 변환 → 없으면 null 처리 (기존 로직)
        Optional<double[]> coords = kakaoGeoUtil.getCoordinates(dto.getAddress());
        Double latitude = coords.map(c -> c[0]).orElse(null);
        Double longitude = coords.map(c -> c[1]).orElse(null);

        // Entity 생성 및 저장 (기존 로직)
        LocalMerchant entity = LocalMerchant.builder()
                .merchantSeq(dto.getMerchantSeq())
                .name(dto.getName())
                .category(dto.getCategory())
                .address(dto.getAddress())
                .tel(dto.getTel())
                .latitude(latitude)
                .longitude(longitude)
                .build();

        try {
            localMerchantRepository.save(entity);
        } catch (Exception e) {
            log.error("❗ 저장 실패 (중복 또는 DB 문제): merchantSeq={}", dto.getMerchantSeq(), e);
        }
    }

    // 🆕 좌표 업데이트가 필요한지 확인
    private boolean needsCoordinateUpdate(LocalMerchant merchant) {
        return (merchant.getLatitude() == null || merchant.getLatitude() == 0.0) ||
                (merchant.getLongitude() == null || merchant.getLongitude() == 0.0);
    }

    // 🆕 좌표 업데이트 메서드
    private void updateMerchantCoordinates(LocalMerchant merchant, String address) {
        try {
            Optional<double[]> coords = kakaoGeoUtil.getCoordinates(address);
            if (coords.isPresent()) {
                merchant.setLatitude(coords.get()[0]);
                merchant.setLongitude(coords.get()[1]);
                localMerchantRepository.save(merchant);
                log.info("✅ 좌표 업데이트 성공: {} → ({}, {})",
                        merchant.getName(), coords.get()[0], coords.get()[1]);
            } else {
                log.warn("❌ 좌표 변환 실패: {} - {}", merchant.getName(), address);
            }
        } catch (Exception e) {
            log.error("💥 좌표 업데이트 오류: {} - {}", merchant.getName(), e.getMessage());
        }
    }
}
