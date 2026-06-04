package mss301.se1911.group.assignment.orderservice.domain.event;

import mss301.se1911.group.assignment.orderservice.domain.model.Order;

public class OrderPaidEvent extends OrderEvent {
    public OrderPaidEvent(Order order) {
        super(order);
    }
}
