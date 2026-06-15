package mss301.se1911.group.assignment.deliveryservice.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateDeliveryCommand(
        UUID orderId,
        String pickupAddress,
        BigDecimal pickupLat,
        BigDecimal pickupLng,
        String dropoffAddress,
        BigDecimal dropoffLat,
        BigDecimal dropoffLng,
        BigDecimal codAmount,
        BigDecimal deliveryFee
) {
}
