package mss301.se1911.group.assignment.orderservice.domain.aggregate;

public class OrderCreatedEvent extends OrderEvent {
    public OrderCreatedEvent(OrderAggregate order) {
        super(order);
    }
}
