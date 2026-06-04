package mss301.se1911.group.assignment.paymentservices.domain.exception;

import java.util.UUID;

public class DuplicatePaymentException extends RuntimeException {

    public DuplicatePaymentException(UUID orderId) {
        super("A pending, processing, or completed payment already exists for order: " + orderId);
    }
}
