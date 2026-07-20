package com.bank.server.repository;

import com.bank.server.entity.Product;
import com.bank.server.enums.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByCategory(ProductCategory category);
    Optional<Product> findByProductName(String productName);

}
