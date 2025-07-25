package com.chungnamthon.cheonon.point.repository;

import com.chungnamthon.cheonon.point.domain.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
    List<Point> findByUserId(Long userId);

    @Query("SELECT COALESCE(SUM(p.changedPoint), 0) FROM Point p WHERE p.user.id = :userId")
    int sumPointByUserId(@Param("userId") Long userId);
}
