package mss301.se1911.group.assignment.paymentservices.api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class PaymentResponse {
    private UUID id;
    private UUID orderId;
    private UUID customerId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private String paymentGateway;
    private String status;
    private String payUrl; // Only populated for PENDING/PROCESSING status for gateways that require redirect
    private OffsetDateTime paidAt;
    private OffsetDateTime createdAt;
}
