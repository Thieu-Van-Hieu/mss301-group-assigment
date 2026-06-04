package mss301.se1911.group.assignment.paymentservices.application.command;

import mss301.se1911.group.assignment.paymentservices.domain.vo.PaymentMethod;

import java.math.BigDecimal;
import java.util.UUID;

public record InitiatePaymentCommand(
        UUID orderId,
        UUID customerId,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        String ipAddress
) {
}
