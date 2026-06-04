package mss301.se1911.group.assignment.orderservice.domain.event;

import mss301.se1911.group.assignment.orderservice.domain.model.Order;

public class OrderConfirmedEvent extends OrderEvent {
    public OrderConfirmedEvent(Order order) {
        super(order);
    }
}
