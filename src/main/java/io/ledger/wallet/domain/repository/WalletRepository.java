package io.ledger.wallet.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.ledger.wallet.domain.entity.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long>{
	
	Optional<Wallet> findByUserId(Long userId);
}
