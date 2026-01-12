package io.ledger.wallet.service.strategy;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component("STANDARD") 
public class StandardFeeStrategy implements FeeStrategy {
    @Override
    public BigDecimal calculateFee(BigDecimal amount) {
        
        return amount.multiply(new BigDecimal("0.01"));
    }
}