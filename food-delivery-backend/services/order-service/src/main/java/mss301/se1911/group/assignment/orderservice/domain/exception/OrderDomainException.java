package mss301.se1911.group.assignment.orderservice.domain.exception;

public class OrderDomainException extends RuntimeException {
    public OrderDomainException(String message) {
        super(message);
    }

    public OrderDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
