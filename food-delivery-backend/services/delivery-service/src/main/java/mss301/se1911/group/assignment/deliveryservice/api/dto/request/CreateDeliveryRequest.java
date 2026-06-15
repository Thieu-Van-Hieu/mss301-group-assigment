package mss301.se1911.group.assignment.deliveryservice.api.dto.request;

import mss301.se1911.group.assignment.deliveryservice.application.command.CreateDeliveryCommand;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateDeliveryRequest(
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
    // Hàm hỗ trợ ánh xạ (Mapping) từ Web Request sang Application Command
    public CreateDeliveryCommand toCommand() {
        return new CreateDeliveryCommand(
                this.orderId,
                this.pickupAddress, this.pickupLat, this.pickupLng,
                this.dropoffAddress, this.dropoffLat, this.dropoffLng,
                this.codAmount, this.deliveryFee
        );
    }
}