package com.chungnamthon.cheonon.users.Repository;

import com.chungnamthon.cheonon.users.Entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<UsersEntity, BigInteger> {
    Optional<UsersEntity> findByEmail(String email);
}