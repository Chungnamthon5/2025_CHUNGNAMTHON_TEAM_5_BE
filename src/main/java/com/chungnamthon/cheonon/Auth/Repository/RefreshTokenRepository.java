package com.chungnamthon.cheonon.Auth.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.chungnamthon.cheonon.Auth.Entity.RefreshTokenEntity;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    // ✅ 수정된 메서드들:
    List<RefreshTokenEntity> findByUser_UserId(BigInteger userId);

    Optional<RefreshTokenEntity> findTopByUser_UserIdOrderByExpiredAtDesc(BigInteger userId);

    void deleteAllByUser_UserId(BigInteger userId);
}