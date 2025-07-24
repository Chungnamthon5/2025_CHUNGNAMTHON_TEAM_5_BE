package com.chungnamthon.cheonon.map.service;

import com.chungnamthon.cheonon.map.domain.Affiliate;
import com.chungnamthon.cheonon.map.dto.AffiliateDto;
import com.chungnamthon.cheonon.map.repository.AffiliateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new EntityNotFoundException("Affiliate not found: " + id));
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
}
