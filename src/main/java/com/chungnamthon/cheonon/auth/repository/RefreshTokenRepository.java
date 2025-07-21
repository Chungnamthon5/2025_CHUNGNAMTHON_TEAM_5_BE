package com.chungnamthon.cheonon.auth.repository;

import com.chungnamthon.cheonon.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    // ✅ 수정된 메서드들:
    List<RefreshToken> findByUser_Id(Long userId);

    Optional<RefreshToken> findTopByUserIdOrderByExpiredAtDesc(BigInteger userId);

    void deleteAllByUser_Id(BigInteger userId);
}