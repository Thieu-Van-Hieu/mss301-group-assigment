package mss301.se1911.group.assignment.orderservice.application.ports.out;

import mss301.se1911.group.assignment.orderservice.domain.event.OrderEvent;

public interface OrderEventPublisher {
    void publish(OrderEvent event);
}
