package com.chungnamthon.cheonon.map.repository;

import com.chungnamthon.cheonon.map.domain.Affiliate;
import com.chungnamthon.cheonon.map.domain.Merchant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AffiliateRepository extends JpaRepository<Affiliate, Long> {
    List<Affiliate> findTop3ByOrderByCreatedAtDesc();
}
