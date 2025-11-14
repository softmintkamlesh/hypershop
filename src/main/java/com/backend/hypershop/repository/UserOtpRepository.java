// UserOtpRepository.java
package com.backend.hypershop.repository;

import com.backend.hypershop.entity.UserOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserOtpRepository extends JpaRepository<UserOtp, Integer> {
    Optional<UserOtp> findByMobile(String mobile);

    // ✅ Find only active OTPs
    Optional<UserOtp> findByMobileAndStatusTrue(String mobile);

    // ✅ Delete inactive OTPs (cleanup job)
    @Modifying
    @Query("DELETE FROM UserOtp o WHERE o.status = false AND o.expiredAt < :cutoffTime")
    int deleteInactiveExpiredOtps(@Param("cutoffTime") LocalDateTime cutoffTime);
}
