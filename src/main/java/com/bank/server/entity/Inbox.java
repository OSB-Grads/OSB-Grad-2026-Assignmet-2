package com.bank.server.entity;

import com.bank.server.enums.InboxStatus;
import com.bank.server.enums.InboxMessageType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inbox")
public class Inbox {

    @Id
    private String id;

    @Column(name = "correlation_id",nullable = false)
    private String correlationId;

    @Column(name = "transaction_id")
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type",nullable = false)
    private InboxMessageType messageType;

    @Lob
    @Column(name = "payload",nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InboxStatus status;

    @Column(name = "reason")
    private String reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();

        if (status == null) {
            status = InboxStatus.PENDING;
        }
    }
}
