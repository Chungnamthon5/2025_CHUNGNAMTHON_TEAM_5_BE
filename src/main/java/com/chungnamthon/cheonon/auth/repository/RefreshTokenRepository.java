package com.chungnamthon.cheonon.auth.repository;

import com.chungnamthon.cheonon.auth.domain.RefreshToken;
import com.chungnamthon.cheonon.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUser_Id(Long userId);

    Optional<RefreshToken> findTopByUserIdOrderByExpiredAtDesc(Long userId);

    void deleteAllByUser_Id(Long userId);

    Optional<RefreshToken> findByUser(User user);
}