package mss301.se1911.group.assignment.paymentservices.api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class WalletLedgerResponse {
    private UUID id;
    private UUID transactionRefId;
    private String entryType;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String description;
    private OffsetDateTime createdAt;
}
