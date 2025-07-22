package com.chungnamthon.cheonon.local_merchant.repository;

import com.chungnamthon.cheonon.local_merchant.domain.LocalMerchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface LocalMerchantRepository extends JpaRepository<LocalMerchant, Long> {
    Optional<LocalMerchant> findByMerchantSeq(BigInteger merchantSeq);
}