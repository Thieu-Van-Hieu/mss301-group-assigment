package mss301.se1911.group.assignment.customerservice.infrastructure.messaging.kafka.event;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Event Order Service phát khi khách hàng đặt đơn thành công (OrderCreated).
 */
@Builder
public record OrderCreatedEvent(
        UUID orderId,
        UUID customerId,
        UUID restaurantId,
        BigDecimal totalAmount,
        String currency,
        String status,
        String itemsSummary,
        ZonedDateTime createdAt
) {}
