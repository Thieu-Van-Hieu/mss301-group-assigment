package mss301.se1911.group.assignment.orderservice.domain.aggregate;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public abstract class OrderEvent {
    private final OrderAggregate order;
    private final LocalDateTime createdAt;

    protected OrderEvent(OrderAggregate order) {
        this.order = order;
        this.createdAt = LocalDateTime.now();
    }
}
