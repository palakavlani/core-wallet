package io.ledger.wallet.api.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferRequestDto {
    private String toUsername; 
    private BigDecimal amount;
}