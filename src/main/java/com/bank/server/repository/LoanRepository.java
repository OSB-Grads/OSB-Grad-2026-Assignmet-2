package com.bank.server.repository;

import com.bank.server.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, String> {

    List<Loan> findByCustomerId(String customerId);

}