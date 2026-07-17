package mss301.se1911.group.assignment.customerservice.domain.repository.criteria;

import java.util.UUID;

/**
 * Điều kiện lọc lịch sử đơn hàng của một khách hàng (theo trạng thái).
 */
public record OrderHistoryQueryCriteria(
        UUID customerId,
        String status
) {}
