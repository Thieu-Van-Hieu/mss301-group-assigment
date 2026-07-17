package mss301.se1911.group.assignment.customerservice.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateAddressCommand(
        UUID addressId,
        String recipientName,
        String phoneNumber,
        String addressLine,
        String ward,
        String district,
        String city,
        BigDecimal latitude,
        BigDecimal longitude
) {}
