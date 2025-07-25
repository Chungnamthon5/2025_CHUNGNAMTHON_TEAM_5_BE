package com.chungnamthon.cheonon.point.repository;

import com.chungnamthon.cheonon.point.domain.PowerUser;
import lombok.Builder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PowerUserRepository extends JpaRepository<PowerUser, Long> {
    List<PowerUser> findTop5ByWeekOfOrderByRankAsc(LocalDate weekOf);
}