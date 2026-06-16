package mss301.se1911.group.assignment.orderservice.domain.aggregate;

import lombok.Getter;

@Getter
public class OrderCancelledEvent extends OrderEvent {
    private final String reason;

    public OrderCancelledEvent(OrderAggregate order, String reason) {
        super(order);
        this.reason = reason;
    }
}
