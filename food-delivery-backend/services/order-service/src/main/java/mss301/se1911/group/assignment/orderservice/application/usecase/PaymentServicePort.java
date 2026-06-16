package mss301.se1911.group.assignment.orderservice.application.usecase;

import mss301.se1911.group.assignment.orderservice.application.command.PaymentInfoDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentServicePort {
    PaymentInfoDto processPayment(UUID orderId, BigDecimal amount, String currency, PaymentInfoDto paymentInfo);
}
