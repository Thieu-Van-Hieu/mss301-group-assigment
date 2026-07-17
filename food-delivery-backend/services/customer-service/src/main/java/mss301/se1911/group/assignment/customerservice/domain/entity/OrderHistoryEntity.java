package mss301.se1911.group.assignment.customerservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Read model lịch sử đơn hàng, được dựng lên từ các event của Order Service.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_history")
public class OrderHistoryEntity {

    @Id
    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "restaurant_id")
    private UUID restaurantId;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "items_summary", columnDefinition = "TEXT")
    private String itemsSummary;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "completed_at")
    private ZonedDateTime completedAt;
}
