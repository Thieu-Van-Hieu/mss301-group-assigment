package mss301.se1911.group.assignment.orderservice.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import mss301.se1911.group.assignment.orderservice.domain.exception.OrderDomainException;

@Getter
@EqualsAndHashCode
@ToString
public class PaymentInfo {
    private final PaymentMethod method;
    private final PaymentStatus status;
    private final String transactionId;

    public PaymentInfo(PaymentMethod method, PaymentStatus status, String transactionId) {
        if (method == null) {
            throw new OrderDomainException("Payment method cannot be null");
        }
        if (status == null) {
            throw new OrderDomainException("Payment status cannot be null");
        }
        this.method = method;
        this.status = status;
        this.transactionId = transactionId;
    }
}
