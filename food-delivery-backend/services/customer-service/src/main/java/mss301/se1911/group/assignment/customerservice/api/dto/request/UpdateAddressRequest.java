package mss301.se1911.group.assignment.customerservice.api.dto.request;

import mss301.se1911.group.assignment.customerservice.application.command.UpdateAddressCommand;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateAddressRequest(
        String recipientName,
        String phoneNumber,
        String addressLine,
        String ward,
        String district,
        String city,
        BigDecimal latitude,
        BigDecimal longitude
) {
    public UpdateAddressCommand toCommand(UUID addressId) {
        return new UpdateAddressCommand(
                addressId, this.recipientName, this.phoneNumber, this.addressLine,
                this.ward, this.district, this.city, this.latitude, this.longitude);
    }
}
