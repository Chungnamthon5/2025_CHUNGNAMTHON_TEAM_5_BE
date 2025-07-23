package com.chungnamthon.cheonon.local_merchant.repository;

import com.chungnamthon.cheonon.local_merchant.domain.LocalMerchant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface LocalMerchantRepository extends JpaRepository<LocalMerchant, Long> {
    Optional<LocalMerchant> findByMerchantSeq(BigInteger merchantSeq);

    // LocalMerchantRepository.java 수정
    @Query("SELECT m FROM LocalMerchant m WHERE " +
            "(m.latitude IS NULL OR m.latitude = 0.0) OR " +
            "(m.longitude IS NULL OR m.longitude = 0.0)")
    List<LocalMerchant> findMerchantsWithInvalidCoordinates();

    @Query("SELECT m FROM LocalMerchant m WHERE " +
            "(m.latitude IS NULL OR m.latitude = 0.0) OR " +
            "(m.longitude IS NULL OR m.longitude = 0.0)")
    Page<LocalMerchant> findMerchantsWithInvalidCoordinates(Pageable pageable);

    @Query("SELECT COUNT(m) FROM LocalMerchant m WHERE " +
            "(m.latitude IS NULL OR m.latitude = 0.0) OR " +
            "(m.longitude IS NULL OR m.longitude = 0.0)")
    long countMerchantsWithInvalidCoordinates();

}
