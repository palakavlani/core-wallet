package io.ledger.wallet.service.strategy;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component("VIP") 
public class VipFeeStrategy implements FeeStrategy {
    @Override
    public BigDecimal calculateFee(BigDecimal amount) {
        return BigDecimal.ZERO;
    }
}
