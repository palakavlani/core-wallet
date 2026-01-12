package io.ledger.wallet.service.strategy;

import java.math.BigDecimal;

public interface FeeStrategy {
    BigDecimal calculateFee(BigDecimal amount);
}