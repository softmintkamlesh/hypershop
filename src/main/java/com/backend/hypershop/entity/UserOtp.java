// UserOtp.java
package com.backend.hypershop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "user_otp",
    uniqueConstraints = {
        @UniqueConstraint(name = "unique_mobile", columnNames = "mobile")
    },
    indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_expired_at", columnList = "expired_at")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "mobile", nullable = false, length = 50, unique = true)
    private String mobile;

    @Column(name = "otp", nullable = false, length = 50)
    private String otp;

    @Column(name = "attempt_count", nullable = false, columnDefinition = "TINYINT UNSIGNED DEFAULT 0")
    @Builder.Default
    private Integer attemptCount = 0;
    private boolean status;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (expiredAt == null) {
            expiredAt = createdAt.plusMinutes(5);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (attemptCount == null) {
            attemptCount = 0;
        }
    }

    /**
     * Check if OTP is expired
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }

    /**
     * Check if max attempts reached
     */
    public boolean isMaxAttemptsReached() {
        return attemptCount >= 3;
    }

    /**
     * Increment attempt count
     */
    public void incrementAttempt() {
        this.attemptCount++;
    }
}
