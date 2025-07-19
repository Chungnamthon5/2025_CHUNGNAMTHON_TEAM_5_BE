package com.chungnamthon.cheonon.localmerchant.Service;

import com.chungnamthon.cheonon.localmerchant.Dto.LocalMerchantDto;
import com.chungnamthon.cheonon.localmerchant.Entity.LocalMerchantEntity;
import com.chungnamthon.cheonon.localmerchant.Repository.LocalMerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocalMerchantService {

    private final LocalMerchantRepository localMerchantRepository;
    private final KakaoGeoUtil kakaoGeoUtil;

    public void saveMerchant(LocalMerchantDto dto) {
        // 이미 저장된 merchant_seq는 무시
        if (localMerchantRepository.findByMerchantSeq(dto.getMerchantSeq()).isPresent()) {
            return;
        }

        // 좌표 변환 → 없으면 null 처리
        Optional<double[]> coords = kakaoGeoUtil.getCoordinates(dto.getAddress());
        Double latitude = coords.map(c -> c[0]).orElse(null);
        Double longitude = coords.map(c -> c[1]).orElse(null);

        // Entity 생성 및 저장
        LocalMerchantEntity entity = LocalMerchantEntity.builder()
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
            System.out.println("❗ 저장 실패 (중복 또는 DB 문제): merchantSeq=" + dto.getMerchantSeq());
        }
    }
}