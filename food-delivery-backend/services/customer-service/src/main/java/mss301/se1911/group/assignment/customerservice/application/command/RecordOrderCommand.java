package mss301.se1911.group.assignment.customerservice.application.command;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Dữ liệu nghiệp vụ để dựng read model lịch sử đơn hàng (ánh xạ từ event của Order Service).
 */
public record RecordOrderCommand(
        UUID orderId,
        UUID customerId,
        UUID restaurantId,
        BigDecimal totalAmount,
        String currency,
        String status,
        String itemsSummary,
        ZonedDateTime createdAt
) {}
