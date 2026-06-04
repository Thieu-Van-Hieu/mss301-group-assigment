package mss301.se1911.group.assignment.paymentservices.domain.aggregate;

import lombok.Getter;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PayoutRecord;
import mss301.se1911.group.assignment.paymentservices.domain.exception.PaymentProcessingException;
import mss301.se1911.group.assignment.paymentservices.domain.vo.PayoutStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate root for PayoutRecord.
 * Encapsulates payout calculation logic (commission splits, validations)
 * and status transitions.
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
        Objects.requireNonNull(totalAmount, "totalAmount must not be null");
        Objects.requireNonNull(deliveryFee, "deliveryFee must not be null");

        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentProcessingException("Total amount must be greater than zero");
        }

        BigDecimal foodAmount = totalAmount.subtract(deliveryFee);

        BigDecimal restaurantCommission = foodAmount
                .multiply(BigDecimal.valueOf(restaurantCommissionRate));
        BigDecimal driverCommission = deliveryFee
                .multiply(BigDecimal.valueOf(driverCommissionRate));

        BigDecimal restaurantPayout = foodAmount
                .subtract(restaurantCommission).setScale(2, RoundingMode.HALF_UP);
        BigDecimal driverPayout = deliveryFee
                .subtract(driverCommission).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPlatformFee = restaurantCommission
                .add(driverCommission).setScale(2, RoundingMode.HALF_UP);

        if (restaurantPayout.compareTo(BigDecimal.ZERO) < 0
                || driverPayout.compareTo(BigDecimal.ZERO) < 0) {
            throw new PaymentProcessingException(
                    "Calculated payout amounts cannot be negative. "
                            + "Restaurant: " + restaurantPayout + ", Driver: " + driverPayout);
        }

        PayoutRecord record = PayoutRecord.builder()
                .orderId(orderId)
                .paymentTxId(paymentTxId)
                .restaurantId(restaurantId)
                .driverId(driverId)
                .totalAmount(totalAmount)
                .deliveryFee(deliveryFee)
                .platformFee(totalPlatformFee)
                .restaurantPayout(restaurantPayout)
                .driverPayout(driverPayout)
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

    // ── Convenience Accessors ──

    public BigDecimal getRestaurantPayout() {
        return record.getRestaurantPayout();
    }

    public BigDecimal getDriverPayout() {
        return record.getDriverPayout();
    }

    public BigDecimal getPlatformFee() {
        return record.getPlatformFee();
    }
}
