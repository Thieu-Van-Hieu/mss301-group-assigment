package mss301.se1911.group.assignment.orderservice.domain.event;

import lombok.Getter;
import mss301.se1911.group.assignment.orderservice.domain.model.Order;

@Getter
public class OrderCancelledEvent extends OrderEvent {
    private final String reason;

    public OrderCancelledEvent(Order order, String reason) {
        super(order);
        this.reason = reason;
    }
}
