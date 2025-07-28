package com.chungnamthon.cheonon.point.repository;

import com.chungnamthon.cheonon.point.domain.Point;
import com.chungnamthon.cheonon.point.domain.value.PaymentType;
import com.chungnamthon.cheonon.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
    Optional<Point> findTopByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT COALESCE(SUM(p.changedPoint), 0) FROM Point p WHERE p.user.id = :userId")
    int sumPointByUserId(@Param("userId") Long userId);

    @Query("""
    SELECT p.user.id, SUM(p.changedPoint)
    FROM Point p
    GROUP BY p.user.id
    ORDER BY SUM(p.changedPoint) DESC
""")
    List<Object[]> findTopUsersByTotalPoint(Pageable pageable);

    List<Point> findByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByUserIdAndPaymentTypeAndCreatedAtBetween(Long userId, PaymentType paymentType, LocalDateTime checkStart, LocalDateTime checkEnd);

    @Query("SELECT COALESCE(SUM(p.changedPoint), 0) FROM Point p WHERE p.user.id = :userId AND p.createdAt BETWEEN :start AND :end")
    int sumPointByUserIdAndPeriod(@Param("userId") Long userId,
                                  @Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end);
    List<Point> findByUserIdAndPaymentTypeAndCreatedAtBetween(
            Long userId, PaymentType type, LocalDateTime start, LocalDateTime end);
}
