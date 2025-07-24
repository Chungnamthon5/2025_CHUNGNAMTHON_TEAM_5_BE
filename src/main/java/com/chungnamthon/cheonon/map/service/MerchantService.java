package com.chungnamthon.cheonon.map.service;

import com.chungnamthon.cheonon.map.domain.Merchant;
import com.chungnamthon.cheonon.map.dto.MerchantDto;
import com.chungnamthon.cheonon.map.repository.MerchantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MerchantService {
    private final MerchantRepository repo;

    public Page<MerchantDto> list(Pageable pageable) {
        return repo.findAll(pageable)
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
        Merchant m = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Merchant not found: " + id));
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
}

