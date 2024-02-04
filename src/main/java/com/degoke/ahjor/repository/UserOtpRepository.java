package com.degoke.ahjor.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;


import com.degoke.ahjor.entity.UserOtp;

public interface UserOtpRepository extends JpaRepository<UserOtp, UUID>{
    Optional<UserOtp> findByUserId(UUID userId);
}
