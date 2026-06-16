package mss301.se1911.group.assignment.orderservice.domain.aggregate;

public class OrderConfirmedEvent extends OrderEvent {
    public OrderConfirmedEvent(OrderAggregate order) {
        super(order);
    }
}
