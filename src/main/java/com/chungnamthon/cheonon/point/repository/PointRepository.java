package com.chungnamthon.cheonon.point.repository;

import com.chungnamthon.cheonon.point.domain.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
    List<Point> findByUserId(Long userId);
}
