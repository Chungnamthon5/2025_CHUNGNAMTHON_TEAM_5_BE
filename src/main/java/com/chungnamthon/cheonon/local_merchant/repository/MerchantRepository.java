package com.chungnamthon.cheonon.local_merchant.repository;

import com.chungnamthon.cheonon.local_merchant.domain.Merchant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    Optional<Merchant> findByMerchantSeq(String merchantSeq);

    @Query("SELECT m FROM Merchant m WHERE " +
            "(m.latitude IS NULL OR m.latitude = 0.0) OR " +
            "(m.longitude IS NULL OR m.longitude = 0.0)")
    List<Merchant> findMerchantsWithInvalidCoordinates();

    @Query("SELECT m FROM Merchant m WHERE " +
            "(m.latitude IS NULL OR m.latitude = 0.0) OR " +
            "(m.longitude IS NULL OR m.longitude = 0.0)")
    Page<Merchant> findMerchantsWithInvalidCoordinates(Pageable pageable);

    @Query("SELECT COUNT(m) FROM Merchant m WHERE " +
            "(m.latitude IS NULL OR m.latitude = 0.0) OR " +
            "(m.longitude IS NULL OR m.longitude = 0.0)")
    long countMerchantsWithInvalidCoordinates();

    @Query("""
        SELECT m FROM Merchant m
         WHERE m.latitude  BETWEEN :southLat AND :northLat
           AND m.longitude BETWEEN :westLng  AND :eastLng
        """)
    List<Merchant> findMerchantsInBounds(
            @Param("southLat") Double southLat,
            @Param("northLat") Double northLat,
            @Param("westLng")  Double westLng,
            @Param("eastLng")  Double eastLng
    );

    //name 이 keyword 와 대소문자 구분 없이 정확히 일치하는 Merchant만 페이징 조회
    Page<Merchant> findByNameEqualsIgnoreCase(String name, Pageable pageable);
}
