package mss301.se1911.group.assignment.paymentservices.application.command;

import java.util.UUID;

public record ConfirmCodPaymentCommand(UUID orderId) {
}
