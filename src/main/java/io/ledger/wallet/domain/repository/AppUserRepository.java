package io.ledger.wallet.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.ledger.wallet.domain.entity.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long>{
	
	Optional<AppUser> findByUsername(String username);
	boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
