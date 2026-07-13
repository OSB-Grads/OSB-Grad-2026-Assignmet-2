package  com.bank.server.repository;
import com.bank.server.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository
        extends JpaRepository<Transaction, String> {
    @Query("""
        SELECT t
        FROM Transaction t
        WHERE t.fromAccount.id = :accountId
           OR t.toAccount.id = :accountId
    """)
    List<Transaction> findByAccountId(@Param("accountId") String accountId);
}