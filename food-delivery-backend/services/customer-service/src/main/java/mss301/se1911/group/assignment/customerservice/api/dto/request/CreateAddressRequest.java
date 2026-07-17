package mss301.se1911.group.assignment.customerservice.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import mss301.se1911.group.assignment.customerservice.application.command.CreateAddressCommand;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateAddressRequest(
        String recipientName,
        String phoneNumber,

        @NotBlank(message = "Địa chỉ chi tiết không được để trống")
        String addressLine,

        String ward,
        String district,
        String city,
        BigDecimal latitude,
        BigDecimal longitude,
        Boolean isDefault
) {
    public CreateAddressCommand toCommand(UUID customerId) {
        return new CreateAddressCommand(
                customerId, this.recipientName, this.phoneNumber, this.addressLine,
                this.ward, this.district, this.city, this.latitude, this.longitude,
                this.isDefault != null && this.isDefault
        );
    }
}
