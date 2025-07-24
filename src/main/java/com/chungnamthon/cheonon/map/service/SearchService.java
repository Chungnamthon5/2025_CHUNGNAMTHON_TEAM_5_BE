package com.chungnamthon.cheonon.map.service;

import com.chungnamthon.cheonon.global.payload.ResponseDto;
import com.chungnamthon.cheonon.map.domain.Affiliate;
import com.chungnamthon.cheonon.map.domain.Merchant;
import com.chungnamthon.cheonon.map.dto.AffiliateDto;
import com.chungnamthon.cheonon.map.dto.MerchantDto;
import com.chungnamthon.cheonon.map.dto.SearchResultDto;
import com.chungnamthon.cheonon.map.repository.AffiliateRepository;
import com.chungnamthon.cheonon.map.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final MerchantRepository merchantRepo;

    public Page<SearchResultDto> searchByName(String keyword, Pageable pageable) {
        Page<Merchant> page = merchantRepo
                .findByNameEqualsIgnoreCase(keyword, pageable);

        return page.map(m -> SearchResultDto.builder()
                .id(m.getId())
                .name(m.getName())
                .address(m.getAddress())
                .tel(m.getTel())
                .category(m.getCategory())
                .lat(m.getLatitude())
                .lng(m.getLongitude())
                .type(m.getIsAffiliate() ? "AFFILIATE" : "MERCHANT")
                .build()
        );
    }
}
