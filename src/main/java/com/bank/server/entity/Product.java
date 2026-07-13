package com.bank.server.entity;

import com.bank.server.enums.ProductCategory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class Product {
    @Id
    private String id;

    @Column(name = "product_name" , nullable = false ,unique = true, length = 14)
    private String productName;

    @Enumerated(EnumType.STRING)
    @Column(name = "category" , nullable = false)
    private ProductCategory category;

    @Column(name = "interest_rate" , precision = 4 ,scale = 2 , nullable = false)
    private BigDecimal interestRate;

    @Column(name = "min_operating_balance" ,precision = 15,scale = 2 , nullable = false)
    private BigDecimal minOperatingBalance;

    @Column(name = "term_months")
    private Long termMonths;
}
