package com.bank.server.repository;

import com.bank.server.entity.LoanCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanCategoryRepository extends JpaRepository<LoanCategory, String> {
}