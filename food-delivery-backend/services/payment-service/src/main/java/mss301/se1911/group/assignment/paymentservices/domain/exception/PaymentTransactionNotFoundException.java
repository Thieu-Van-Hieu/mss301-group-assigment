package mss301.se1911.group.assignment.paymentservices.domain.exception;

import java.util.UUID;

public class PaymentTransactionNotFoundException extends RuntimeException {

    public PaymentTransactionNotFoundException(UUID paymentTxId) {
        super("Payment transaction not found with id: " + paymentTxId);
    }

    public PaymentTransactionNotFoundException(String field, UUID value) {
        super("Payment transaction not found with " + field + ": " + value);
    }
}
