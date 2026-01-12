package io.ledger.wallet.api.dto;

import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
public class AuditLogMessage implements Serializable {
    private String sender;
    private String receiver;
    private BigDecimal amount;
    private String timestamp; 
}