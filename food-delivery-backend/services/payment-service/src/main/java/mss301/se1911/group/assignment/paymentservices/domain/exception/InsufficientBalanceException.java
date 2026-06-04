package mss301.se1911.group.assignment.paymentservices.domain.exception;

import java.math.BigDecimal;
import java.util.UUID;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(UUID walletId, BigDecimal requested, BigDecimal actual) {
        super(String.format("Insufficient balance in wallet %s. Requested: %s, Actual: %s", 
                walletId, requested, actual));
    }
}
