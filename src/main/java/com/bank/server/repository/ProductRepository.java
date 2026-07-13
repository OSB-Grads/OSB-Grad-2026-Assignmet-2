package com.bank.server.repository;

import com.bank.server.entity.Product;
import com.bank.server.enums.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByCategory(ProductCategory category);

    @Query("SELECT DISTINCT p.category FROM Product p")
    List<String> findAllCategories();
}
