package com.bank.server.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

//    @Column(name="from_account_id")
//    private String fromAccountId;
//
//    @Column(name="to_account_id")
//    private String toAccountId;
//
//     @Column(name="customer_id")
//    private String customerId;

    @Column(nullable = false)
    private String transactionType;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    private String status;
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

    @OneToOne
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;

    @OneToOne
    @JoinColumn(name = "to_account_id")
    private Account toAccount;

}
