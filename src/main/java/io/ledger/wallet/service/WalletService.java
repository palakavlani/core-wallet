package io.ledger.wallet.service;

import io.ledger.wallet.api.dto.AuditLogMessage;
import io.ledger.wallet.api.dto.TransferRequestDto;
import io.ledger.wallet.api.dto.WalletDto;
import io.ledger.wallet.config.RabbitMQConfig;
import io.ledger.wallet.domain.entity.AppUser;
import io.ledger.wallet.domain.entity.Transaction;
import io.ledger.wallet.domain.entity.Wallet;
import io.ledger.wallet.domain.enums.TransactionType;
import io.ledger.wallet.domain.enums.UserType;
import io.ledger.wallet.domain.repository.AppUserRepository;
import io.ledger.wallet.domain.repository.TransactionRepository;
import io.ledger.wallet.domain.repository.WalletRepository;
import io.ledger.wallet.service.strategy.FeeStrategy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final AppUserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final Map<String, FeeStrategy> feeStrategies;
    private final RabbitTemplate rabbitTemplate;
    
    @Cacheable(value = "wallets", key = "#username")
    public WalletDto getMyBalance(String username) {
    	System.out.println(">>> Fetching WalletDto from Database for: " + username);
    	
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        return WalletDto.builder()
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .build();
    }
    
    @Transactional 
    @CachePut(value = "wallets", key = "#username")
    public WalletDto deposit(String username, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Deposit amount must be positive");
        }

        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.DEPOSIT)
                .amount(amount)
                .description("Self Deposit")
                .build();
        
        transactionRepository.save(transaction);

        return WalletDto.builder()
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .build();
    }
    
    @Transactional 
    @Caching(evict = {
            @CacheEvict(value = "wallets", key = "#senderUsername"),      // Delete Sender's cached DTO
            @CacheEvict(value = "wallets", key = "#request.toUsername")   // Delete Receiver's cached DTO
        })
    public void transfer(String senderUsername, TransferRequestDto request) {
        BigDecimal amount = request.getAmount();
        String receiverUsername = request.getToUsername();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Transfer amount must be positive");
        }
        if (senderUsername.equals(receiverUsername)) {
            throw new RuntimeException("Cannot transfer money to yourself");
        }

        AppUser sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        Wallet senderWallet = walletRepository.findByUserId(sender.getId())
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

        AppUser receiver = userRepository.findByUsername(receiverUsername)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
        Wallet receiverWallet = walletRepository.findByUserId(receiver.getId())
                .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

        UserType userType = sender.getType(); 
        
      
        FeeStrategy feeStrategy = feeStrategies.get(userType.name());
        
        if (feeStrategy == null) {
            feeStrategy = feeStrategies.get("STANDARD");
        }

        BigDecimal fee = feeStrategy.calculateFee(amount);
        BigDecimal totalDeduction = amount.add(fee);

        if (senderWallet.getBalance().compareTo(totalDeduction) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        senderWallet.setBalance(senderWallet.getBalance().subtract(totalDeduction));
        receiverWallet.setBalance(receiverWallet.getBalance().add(amount));

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

    
        Transaction senderTx = Transaction.builder()
                .wallet(senderWallet)
                .type(TransactionType.TRANSFER)
                .amount(totalDeduction.negate()) 
                .description("Transfer to " + receiverUsername + " (Fee: " + fee + ")")
                .build();

        Transaction receiverTx = Transaction.builder()
                .wallet(receiverWallet)
                .type(TransactionType.TRANSFER)
                .amount(amount)
                .description("Transfer from " + senderUsername)
                .build();

        transactionRepository.save(senderTx);
        transactionRepository.save(receiverTx);
        
        AuditLogMessage auditMessage = AuditLogMessage.builder()
                .sender(senderUsername)
                .receiver(request.getToUsername())
                .amount(request.getAmount())
                .timestamp(LocalDateTime.now().toString())
                .build();

        
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME, 
                RabbitMQConfig.ROUTING_KEY,   
                auditMessage                  
        );
        
        System.out.println("Message sent to RabbitMQ!");
    }
}