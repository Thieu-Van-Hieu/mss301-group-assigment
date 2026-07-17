package mss301.se1911.group.assignment.customerservice.infrastructure.messaging.kafka.event;

import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Event Order Service phát khi đơn hàng hoàn tất (OrderCompleted).
 */
@Builder
public record OrderCompletedEvent(
        UUID orderId,
        UUID customerId,
        ZonedDateTime completedAt
) {}
