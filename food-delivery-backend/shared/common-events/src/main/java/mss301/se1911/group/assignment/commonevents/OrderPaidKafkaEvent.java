package mss301.se1911.group.assignment.commonevents;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderPaidKafkaEvent(
    UUID orderId,
    UUID customerId,
    BigDecimal amount,
    String currency,
    String address,
    String phone
) {}
