package mss301.se1911.group.assignment.commonevents;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderCompletedKafkaEvent(
    UUID orderId,
    UUID restaurantId,
    UUID driverId,
    BigDecimal totalAmount,
    BigDecimal deliveryFee
) {}
