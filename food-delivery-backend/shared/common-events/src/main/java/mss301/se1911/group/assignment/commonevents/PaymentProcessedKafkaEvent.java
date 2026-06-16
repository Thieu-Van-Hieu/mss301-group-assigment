package mss301.se1911.group.assignment.commonevents;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentProcessedKafkaEvent(
    UUID orderId,
    String transactionId,
    BigDecimal amount,
    String currency,
    String status
) {}
