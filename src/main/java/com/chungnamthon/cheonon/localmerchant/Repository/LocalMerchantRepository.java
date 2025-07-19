package com.chungnamthon.cheonon.localmerchant.Repository;

import com.chungnamthon.cheonon.localmerchant.Entity.LocalMerchantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface LocalMerchantRepository extends JpaRepository<LocalMerchantEntity, Long> {
    Optional<LocalMerchantEntity> findByMerchantSeq(BigInteger merchantSeq);
}