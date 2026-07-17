package com.bank.server.entity;

import com.bank.server.enums.PaymentStatus;
import com.bank.server.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_queue")
public class PaymentQueue {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type",nullable = false)
    private PaymentType type;

    @Column(name = "target_account_id",nullable = false)
    private String targetAccountId;

    @Column(name = "real_world_account_id",nullable = false)
    private String realWorldAccountId;

    @Column(name = "amount",precision = 15,scale =2,nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();

        if (status == null) {
            status = PaymentStatus.PENDING;
        }
    }
}
