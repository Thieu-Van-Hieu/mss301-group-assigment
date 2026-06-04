package mss301.se1911.group.assignment.orderservice.domain.event;

import lombok.Getter;
import mss301.se1911.group.assignment.orderservice.domain.model.Order;
import java.time.LocalDateTime;

@Getter
public abstract class OrderEvent {
    private final Order order;
    private final LocalDateTime createdAt;

    protected OrderEvent(Order order) {
        this.order = order;
        this.createdAt = LocalDateTime.now();
    }
}
