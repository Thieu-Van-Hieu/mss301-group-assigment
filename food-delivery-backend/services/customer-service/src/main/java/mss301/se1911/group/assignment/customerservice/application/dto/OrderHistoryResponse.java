package mss301.se1911.group.assignment.customerservice.application.dto;

import mss301.se1911.group.assignment.customerservice.domain.entity.OrderHistoryEntity;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public record OrderHistoryResponse(
        UUID orderId,
        UUID customerId,
        UUID restaurantId,
        String status,
        BigDecimal totalAmount,
        String currency,
        String itemsSummary,
        ZonedDateTime createdAt,
        ZonedDateTime completedAt
) {
    public static OrderHistoryResponse fromEntity(OrderHistoryEntity e) {
        if (e == null) return null;
        return new OrderHistoryResponse(
                e.getOrderId(), e.getCustomerId(), e.getRestaurantId(), e.getStatus(),
                e.getTotalAmount(), e.getCurrency(), e.getItemsSummary(),
                e.getCreatedAt(), e.getCompletedAt()
        );
    }
}
