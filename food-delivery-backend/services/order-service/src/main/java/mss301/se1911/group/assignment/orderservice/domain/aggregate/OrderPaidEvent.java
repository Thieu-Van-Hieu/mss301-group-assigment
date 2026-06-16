package mss301.se1911.group.assignment.orderservice.domain.aggregate;

public class OrderPaidEvent extends OrderEvent {
    public OrderPaidEvent(OrderAggregate order) {
        super(order);
    }
}
