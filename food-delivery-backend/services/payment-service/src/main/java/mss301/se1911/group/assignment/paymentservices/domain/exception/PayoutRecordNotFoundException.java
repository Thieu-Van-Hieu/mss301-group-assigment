package mss301.se1911.group.assignment.paymentservices.domain.exception;

public class PayoutRecordNotFoundException extends RuntimeException {
    public PayoutRecordNotFoundException(String field, Object value) {
        super(String.format("Payout record not found with %s: '%s'", field, value));
    }
}
