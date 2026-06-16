package mss301.se1911.group.assignment.commonevents;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentFailedKafkaEvent(
    UUID orderId,
    BigDecimal amount,
    String reason
) {}
