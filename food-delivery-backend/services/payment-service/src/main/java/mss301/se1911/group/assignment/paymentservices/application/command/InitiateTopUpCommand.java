package mss301.se1911.group.assignment.paymentservices.application.command;

import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction.PaymentGateway;

import java.math.BigDecimal;
import java.util.UUID;

public record InitiateTopUpCommand(
        UUID customerId,
        BigDecimal amount,
        PaymentGateway gatewayMethod,
        String ipAddress
) {
}
