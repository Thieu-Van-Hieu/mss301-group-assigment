package mss301.se1911.group.assignment.paymentservices.domain.aggregate;

import lombok.Getter;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PayoutRecord;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PayoutRecord.PayoutStatus;
import mss301.se1911.group.assignment.paymentservices.domain.vo.PayoutBreakdown;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate root for PayoutRecord.
 * Delegates payout calculation to the {@link PayoutBreakdown} Value Object
 * and manages status transitions.
 */
@Getter
public class PayoutRecordAggregate {

    private final PayoutRecord record;

    private PayoutRecordAggregate(PayoutRecord record) {
        this.record = record;
    }

    /**
     * Wraps an existing payout record loaded from persistence.
     */
    public static PayoutRecordAggregate from(PayoutRecord record) {
        Objects.requireNonNull(record, "PayoutRecord must not be null");
        return new PayoutRecordAggregate(record);
    }

    /**
     * Factory: calculate payout splits and create a new PayoutRecord in PENDING status.
     * Calculation logic is delegated to the {@link PayoutBreakdown} VO.
     *
     * @param restaurantCommissionRate e.g. 0.15 = 15%
     * @param driverCommissionRate     e.g. 0.10 = 10%
     */
    public static PayoutRecordAggregate calculate(UUID orderId, UUID paymentTxId,
                                                  UUID restaurantId, UUID driverId,
                                                  BigDecimal totalAmount, BigDecimal deliveryFee,
                                                  double restaurantCommissionRate,
                                                  double driverCommissionRate) {
        Objects.requireNonNull(orderId, "orderId must not be null");
        Objects.requireNonNull(paymentTxId, "paymentTxId must not be null");
        Objects.requireNonNull(restaurantId, "restaurantId must not be null");
        Objects.requireNonNull(driverId, "driverId must not be null");

        PayoutBreakdown breakdown = PayoutBreakdown.calculate(
                totalAmount, deliveryFee,
                restaurantCommissionRate, driverCommissionRate
        );

        PayoutRecord record = PayoutRecord.builder()
                .orderId(orderId)
                .paymentTxId(paymentTxId)
                .restaurantId(restaurantId)
                .driverId(driverId)
                .breakdown(breakdown)
                .status(PayoutStatus.PENDING)
                .build();

        return new PayoutRecordAggregate(record);
    }

    // ── State Transition ──

    /**
     * Marks the payout as COMPLETED with a processed timestamp.
     * Idempotent: returns silently if already COMPLETED.
     */
    public void markCompleted() {
        if (record.getStatus() == PayoutStatus.COMPLETED) {
            return; // idempotent
        }
        record.setStatus(PayoutStatus.COMPLETED);
        record.setProcessedAt(OffsetDateTime.now());
    }

    // ── Convenience Accessors (delegates to VO) ──

    public BigDecimal getRestaurantPayout() {
        return record.getBreakdown().getRestaurantPayout();
    }

    public BigDecimal getDriverPayout() {
        return record.getBreakdown().getDriverPayout();
    }

    public BigDecimal getPlatformFee() {
        return record.getBreakdown().getPlatformFee();
    }
}
