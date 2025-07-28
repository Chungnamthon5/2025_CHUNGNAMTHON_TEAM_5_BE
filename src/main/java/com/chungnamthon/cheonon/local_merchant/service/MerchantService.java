package com.chungnamthon.cheonon.local_merchant.service;

import com.chungnamthon.cheonon.global.exception.BusinessException;
import com.chungnamthon.cheonon.global.exception.error.MapError;
import com.chungnamthon.cheonon.local_merchant.domain.Merchant;
import com.chungnamthon.cheonon.local_merchant.dto.LocalMerchantDto;
import com.chungnamthon.cheonon.local_merchant.dto.MerchantDto;
import com.chungnamthon.cheonon.local_merchant.repository.MerchantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Slf4j  //로깅을 위해 추가
@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepository merchantRepository;
    private final KakaoGeoUtil kakaoGeoUtil;

    //좌표 업데이트 모드 설정
    @Value("${merchant.update-coordinates:false}")
    private boolean updateCoordinatesMode;

    public void saveMerchant(LocalMerchantDto dto) {
        Optional<Merchant> existing = merchantRepository.findByMerchantSeq(dto.getMerchantSeq());

        if (existing.isPresent()) {
            //업데이트 모드가 활성화된 경우 좌표 업데이트 시도
            if (updateCoordinatesMode) {
                Merchant merchant = existing.get();
                if (needsCoordinateUpdate(merchant)) {
                    updateMerchantCoordinates(merchant, dto.getAddress());
                }
            }
            return;
        }

        // 좌표 변환 → 없으면 null 처리
        Optional<double[]> coords = kakaoGeoUtil.getCoordinates(dto.getAddress());
        Double latitude = coords.map(c -> c[0]).orElse(null);
        Double longitude = coords.map(c -> c[1]).orElse(null);

        // Entity 생성 및 저장
        Merchant entity = Merchant.builder()
                .merchantSeq(dto.getMerchantSeq())
                .name(dto.getName())
                .category(dto.getCategory())
                .address(dto.getAddress())
                .tel(dto.getTel())
                .latitude(latitude)
                .longitude(longitude)
                .build();

        try {
            merchantRepository.save(entity);
        } catch (Exception e) {
            log.error("저장 실패 (중복 또는 DB 문제): merchantSeq={}", dto.getMerchantSeq(), e);
        }
    }

    //좌표 업데이트가 필요한지 확인
    private boolean needsCoordinateUpdate(Merchant merchant) {
        return (merchant.getLatitude() == null || merchant.getLatitude() == 0.0) ||
                (merchant.getLongitude() == null || merchant.getLongitude() == 0.0);
    }

    //좌표 업데이트 메서드
    private void updateMerchantCoordinates(Merchant merchant, String address) {
        try {
            Optional<double[]> coords = kakaoGeoUtil.getCoordinates(address);
            if (coords.isPresent()) {
                merchant.setLatitude(coords.get()[0]);
                merchant.setLongitude(coords.get()[1]);
                merchantRepository.save(merchant);
                log.info("좌표 업데이트 성공: {} → ({}, {})",
                        merchant.getName(), coords.get()[0], coords.get()[1]);
            } else {
                log.warn("좌표 변환 실패: {} - {}", merchant.getName(), address);
            }
        } catch (Exception e) {
            log.error("좌표 업데이트 오류: {} - {}", merchant.getName(), e.getMessage());
        }
    }

    public Page<MerchantDto> list(Pageable pageable) {
        return merchantRepository.findAll(pageable)
                .map(m -> MerchantDto.builder()
                        .id(m.getId())
                        .merchantSeq(m.getMerchantSeq())
                        .name(m.getName())
                        .address(m.getAddress())
                        .tel(m.getTel())
                        .category(m.getCategory())
                        .lat(m.getLatitude())
                        .lng(m.getLongitude())
                        .isAffiliate(m.getIsAffiliate())
                        .build());
    }

    public MerchantDto detail(Long id) {
        Merchant m = merchantRepository.findById(id)
                .orElseThrow(() -> new BusinessException(MapError.MERCHANT_NOT_FOUND));
        return MerchantDto.builder()
                .id(m.getId())
                .merchantSeq(m.getMerchantSeq())
                .name(m.getName())
                .address(m.getAddress())
                .tel(m.getTel())
                .category(m.getCategory())
                .lat(m.getLatitude())
                .lng(m.getLongitude())
                .isAffiliate(m.getIsAffiliate())
                .build();
    }
    public List<MerchantDto> getMerchantsInBounds(
            Double swLat, Double swLng,
            Double neLat, Double neLng) {

        // 0페이지(인덱스), 최대 50개
        Pageable limit50 = PageRequest.of(0, 50);
        return merchantRepository
                .findMerchantsInBounds(swLat, neLat, swLng, neLng, limit50)
                .getContent()
                .stream()
                .map(MerchantDto::fromEntity)
                .collect(toList());
    }
}
