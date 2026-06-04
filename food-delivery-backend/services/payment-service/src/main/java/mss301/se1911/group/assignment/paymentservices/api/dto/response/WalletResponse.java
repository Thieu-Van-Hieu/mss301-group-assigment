package mss301.se1911.group.assignment.paymentservices.api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class WalletResponse {
    private UUID id;
    private UUID ownerId;
    private String ownerType;
    private BigDecimal balance;
    private String currency;
    private String status;
}
