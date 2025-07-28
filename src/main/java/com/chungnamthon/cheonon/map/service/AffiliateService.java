package com.chungnamthon.cheonon.map.service;

import com.chungnamthon.cheonon.global.exception.BusinessException;
import com.chungnamthon.cheonon.global.exception.error.MapError;
import com.chungnamthon.cheonon.map.domain.Affiliate;
import com.chungnamthon.cheonon.map.dto.AffiliateDto;
import com.chungnamthon.cheonon.map.dto.AffiliateHomePreviewResponse;
import com.chungnamthon.cheonon.map.dto.AffiliatePreviewResponse;
import com.chungnamthon.cheonon.map.repository.AffiliateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AffiliateService {
    private final AffiliateRepository repo;

    public Page<AffiliateDto> list(Pageable pageable) {
        return repo.findAll(pageable)
                .map(a -> AffiliateDto.builder()
                        .id(a.getId())
                        .merchantSeq(a.getMerchantSeq())
                        .name(a.getName())
                        .address(a.getAddress())
                        .tel(a.getTel())
                        .category(a.getCategory())
                        .lat(a.getLatitude())
                        .lng(a.getLongitude())
                        .createAt(a.getCreatedAt())
                        .updateAt(a.getUpdatedAt())
                        .build());
    }

    public AffiliateDto detail(Long id) {
        Affiliate a = repo.findById(id)
                .orElseThrow(() -> new BusinessException(MapError.AFFILIATE_NOT_FOUND));
        return AffiliateDto.builder()
                .id(a.getId())
                .merchantSeq(a.getMerchantSeq())
                .name(a.getName())
                .address(a.getAddress())
                .tel(a.getTel())
                .category(a.getCategory())
                .lat(a.getLatitude())
                .lng(a.getLongitude())
                .createAt(a.getCreatedAt())
                .updateAt(a.getUpdatedAt())
                .build();
    }

    private final AffiliateRepository affiliateRepository;

    // 홈 화면용 간단 정보 (이름만)
    public List<AffiliateHomePreviewResponse> getAffiliateList() {
        return affiliateRepository.findAll()
                .stream()
                .map(AffiliateHomePreviewResponse::from)
                .toList();
    }

    // 제휴 업체 전체 목록 or 상세용
    public List<AffiliatePreviewResponse> getAllAffiliates() {
        return affiliateRepository.findAll()
                .stream()
                .map(AffiliatePreviewResponse::from)
                .toList();
    }
}
