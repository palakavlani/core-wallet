package io.ledger.wallet.domain.repository;

import io.ledger.wallet.domain.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	
    List<Transaction> findByWalletIdOrderByTimestampDesc(Long walletId);
}