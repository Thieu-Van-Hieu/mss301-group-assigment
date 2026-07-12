package mss301.se1911.group.assignment.paymentservices.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mss301.se1911.group.assignment.paymentservices.domain.vo.PayoutBreakdown;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "payout_records")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayoutRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "payment_tx_id", nullable = false)
    private UUID paymentTxId;

    @Column(name = "restaurant_id", nullable = false)
    private UUID restaurantId;

    @Column(name = "driver_id", nullable = false)
    private UUID driverId;

    @Embedded
    private PayoutBreakdown breakdown;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private PayoutStatus status = PayoutStatus.PENDING;

    @Column(name = "processed_at")
    private OffsetDateTime processedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "driver_settlement_status", nullable = false, length = 20)
    @Builder.Default
    private SettlementStatus driverSettlementStatus = SettlementStatus.PENDING;

    @Column(name = "driver_settled_at")
    private OffsetDateTime driverSettledAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "restaurant_settlement_status", nullable = false, length = 20)
    @Builder.Default
    private SettlementStatus restaurantSettlementStatus = SettlementStatus.PENDING;

    @Column(name = "restaurant_settled_at")
    private OffsetDateTime restaurantSettledAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    // ──────────────────────────────────────────────
    // Status enum — lifecycle state of this entity,
    // NOT a Value Object.
    // ──────────────────────────────────────────────

    /**
     * Lifecycle status of a payout record.
     */
    public enum PayoutStatus {
        PENDING,
        COMPLETED,
        FAILED
    }

    /**
     * Settlement status of a payout record.
     */
    public enum SettlementStatus {
        PENDING,
        SETTLED
    }
}
