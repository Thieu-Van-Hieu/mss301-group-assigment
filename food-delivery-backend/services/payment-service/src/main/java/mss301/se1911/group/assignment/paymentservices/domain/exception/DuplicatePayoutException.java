package mss301.se1911.group.assignment.paymentservices.domain.exception;

import java.util.UUID;

public class DuplicatePayoutException extends RuntimeException {
    public DuplicatePayoutException(UUID orderId) {
        super(String.format("Payout already processed for order %s", orderId));
    }
}
