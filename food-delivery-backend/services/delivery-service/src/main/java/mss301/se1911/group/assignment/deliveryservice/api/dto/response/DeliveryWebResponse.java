package mss301.se1911.group.assignment.deliveryservice.api.dto.response;

import mss301.se1911.group.assignment.deliveryservice.application.dto.DeliveryResponse;

import java.math.BigDecimal;
import java.util.UUID;

public record DeliveryWebResponse(
        UUID id,
        UUID orderId,
        String status,
        String pickupAddress,
        String dropoffAddress,
        BigDecimal deliveryFee
) {
    // Hàm hỗ trợ ánh xạ từ Application DTO sang Web Response
    public static DeliveryWebResponse fromAppDto(DeliveryResponse appDto) {
        if (appDto == null) return null;
        return new DeliveryWebResponse(
                appDto.id(),
                appDto.orderId(),
                appDto.status(),
                appDto.pickupAddress(),
                appDto.dropoffAddress(),
                appDto.deliveryFee()
        );
    }
}