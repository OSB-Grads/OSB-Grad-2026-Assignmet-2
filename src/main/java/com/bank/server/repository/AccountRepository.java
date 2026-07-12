package com.bank.server.repository;

import org.springframework.stereotype.Repository;
import com.bank.server.entity.Account;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByCustomerId(String customerId);

    List<Account> findByProductId(String productId);

    @Modifying
    @Query("UPDATE Account a SET a.isLocked = true, a.status = 'ACTIVE' WHERE a.id = :id")
    int lockAccount(@Param("id") String id);

    @Modifying
    @Query("UPDATE Account a SET a.isLocked = false, a.status = 'ACTIVE' WHERE a.id = :id")
    int unlockAccount(@Param("id") String id);

    @Query(value = "SELECT " +
                            "a.id, a.account_number,a.customer_id, a.product_id, " +
                            "a.balance, a.status, a.is_locked, " +
                            "p.product_name, p.category " +
                            "FROM accounts a " +
                            "JOIN products p " +
                            "ON a.product_id = p.id " +
                            "WHERE a.customer_id =:customerId",nativeQuery = true)
    List<Map<String, Object>> getAccountsWithProductByCustomerId(@Param("customerId") String customerId);
}