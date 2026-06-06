package mss301.se1911.group.assignment.orderservice.domain.event;

import mss301.se1911.group.assignment.orderservice.domain.model.Order;

public class OrderCreatedEvent extends OrderEvent {
    public OrderCreatedEvent(Order order) {
        super(order);
    }
}
