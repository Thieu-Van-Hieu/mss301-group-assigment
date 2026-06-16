package mss301.se1911.group.assignment.orderservice.application.usecase;

import mss301.se1911.group.assignment.orderservice.domain.aggregate.OrderEvent;

public interface OrderEventPublisher {
    void publish(OrderEvent event);
}
