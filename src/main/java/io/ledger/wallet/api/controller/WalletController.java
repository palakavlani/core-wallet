package io.ledger.wallet.api.controller;

import io.ledger.wallet.api.dto.DepositRequestDto;
import io.ledger.wallet.api.dto.TransferRequestDto;
import io.ledger.wallet.api.dto.WalletDto;
import io.ledger.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    public ResponseEntity<WalletDto> getMyWallet() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        WalletDto walletDto = walletService.getMyBalance(username);
        
        return ResponseEntity.ok(walletDto);
    }
    
    @PostMapping("/deposit")
    public ResponseEntity<WalletDto> depositMoney(@RequestBody DepositRequestDto request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        WalletDto updatedWallet = walletService.deposit(username, request.getAmount());

        return ResponseEntity.ok(updatedWallet);
    }
    
    @PostMapping("/transfer")
    public ResponseEntity<String> transferMoney(@RequestBody TransferRequestDto request) {
     
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String senderUsername = authentication.getName();
        walletService.transfer(senderUsername, request);

        return ResponseEntity.ok("Transfer successful");
    }
}