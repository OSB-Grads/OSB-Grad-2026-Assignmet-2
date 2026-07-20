package com.bank.server.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bank.server.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    private String id;

    @Column(nullable = false)
    private String transactionType;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TransactionStatus status;

    private String description;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prepersist(){createdAt=LocalDateTime.now();}
    @PreUpdate
    public void preupdate(){updatedAt=LocalDateTime.now();}


    @ManyToOne// this many tro on s basically one customer id can have multiple transctions
    @JoinColumn(name = "customer_id")//this customer_id is column from my transcation table
    private Customer customer;//its just we are saying get primary key from this customer table

    @ManyToOne
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_account_id")
    private Account toAccount;

}
