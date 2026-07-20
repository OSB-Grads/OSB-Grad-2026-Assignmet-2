package  com.bank.server.repository;
import com.bank.server.dto.TransactionDTO;
import com.bank.server.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository
        extends JpaRepository<Transaction, String> {
    List<Transaction> findByCustomerId(String customerId);
    @Query("""
        SELECT t
        FROM Transaction t
        WHERE t.fromAccount.id = :accountId
           OR t.toAccount.id = :accountId
    """)
    List<Transaction> findByAccountId(@Param("accountId") String accountId);

    @Query("""
            SELECT COUNT(t)
            FROM Transaction t
            WHERE t.fromAccount.id = :accountId
              AND t.transactionType = 'TRANSFER'
              AND t.status = 'COMPLETED'
              AND t.createdAt >= :startDate
              AND t.createdAt < :endDate
            """)
    long countCompletedOutgoingTransfers(
            @Param("accountId")
            String accountId,

            @Param("startDate")
            LocalDateTime startDate,

            @Param("endDate")
            LocalDateTime endDate
    );
}