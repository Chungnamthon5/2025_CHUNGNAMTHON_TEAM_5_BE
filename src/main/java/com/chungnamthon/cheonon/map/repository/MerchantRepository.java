package com.chungnamthon.cheonon.map.repository;


import com.chungnamthon.cheonon.map.domain.Affiliate;
import com.chungnamthon.cheonon.map.domain.Merchant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, Long>{
    //name 이 keyword 와 대소문자 구분 없이 정확히 일치하는 Merchant만 페이징 조회
    Page<Merchant> findByNameEqualsIgnoreCase(String name, Pageable pageable);
}
