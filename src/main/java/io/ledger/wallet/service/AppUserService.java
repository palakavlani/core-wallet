package io.ledger.wallet.service;

import io.ledger.wallet.api.dto.RegisterRequestDto;
import io.ledger.wallet.domain.entity.AppUser;
import io.ledger.wallet.domain.entity.Wallet;
import io.ledger.wallet.domain.enums.UserType; 
import io.ledger.wallet.domain.repository.AppUserRepository;
import io.ledger.wallet.domain.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor //Injects dependencies (Repositories) automatically
public class AppUserService {

    private final AppUserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional 
    public AppUser registerUser(RegisterRequestDto request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        UserType type = UserType.STANDARD; 
        
        if (request.getUserType() != null && request.getUserType().equalsIgnoreCase("VIP")) {
            type = UserType.VIP;
        }
        
        AppUser user = AppUser.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .type(type)
                .build();

        AppUser savedUser = userRepository.save(user);

        Wallet wallet = Wallet.builder()
                .user(savedUser)
                .balance(BigDecimal.ZERO)
                .currency("USD") 
                .build();

        walletRepository.save(wallet);

        return savedUser;
    }
}