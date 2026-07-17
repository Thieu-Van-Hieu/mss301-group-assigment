package mss301.se1911.group.assignment.paymentservices.domain.exception;

import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction.PaymentGateway;

public class InvalidTopUpMethodException extends RuntimeException {

    public InvalidTopUpMethodException(PaymentGateway method) {
        super(String.format("Cannot use %s as top-up payment gateway. Only SEPAY, PAYOS are allowed.", method));
    }
}
