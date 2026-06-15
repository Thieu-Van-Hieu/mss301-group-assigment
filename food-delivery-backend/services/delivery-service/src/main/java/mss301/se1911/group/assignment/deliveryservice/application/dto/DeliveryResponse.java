package mss301.se1911.group.assignment.deliveryservice.application.dto;

import mss301.se1911.group.assignment.deliveryservice.domain.entity.DeliveryEntity;

import java.math.BigDecimal;
import java.util.UUID;

public record DeliveryResponse(
        UUID id,
        UUID orderId,
        String status,
        String pickupAddress,
        String dropoffAddress,
        BigDecimal deliveryFee
) {
    public static DeliveryResponse fromEntity(DeliveryEntity entity) {
        return new DeliveryResponse(
                entity.getId(), entity.getOrderId(), entity.getStatus().name(),
                entity.getPickupAddress(), entity.getDropoffAddress(), entity.getDeliveryFee()
        );
    }
}
