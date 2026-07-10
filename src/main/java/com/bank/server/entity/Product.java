package com.bank.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class Product {
    @Id
    private String id;

    @Column(name = "product_name" , nullable = false)
    private String productName;

    @Column(name = "category" , nullable = false)
    private String category;

    @Column(name = "interest_rate" , precision = 4 ,scale = 2 , nullable = false)
    private BigDecimal interestRate;

    @Column(name = "min_operating_balance" ,precision = 15,scale = 2 , nullable = false)
    private BigDecimal minOperatingBalance;

    @Column(name = "term_months")
    private Long termMonths;
}
