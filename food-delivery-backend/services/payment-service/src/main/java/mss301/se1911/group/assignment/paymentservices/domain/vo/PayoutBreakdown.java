package mss301.se1911.group.assignment.paymentservices.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object representing the financial breakdown of an order payout.
 * <p>
 * Immutable — once calculated, cannot be modified.
 * Equality is determined by all five monetary fields.
 * <p>
 * Business rules:
 * <ul>
 *   <li>All amounts must be non-negative</li>
 *   <li>restaurantPayout + driverPayout + platformFee ≈ totalAmount</li>
 *   <li>Commission rates must be between 0 and 1 (exclusive)</li>
 * </ul>
 */
@Embeddable
public final class PayoutBreakdown {

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "delivery_fee", nullable = false, precision = 19, scale = 2)
    private BigDecimal deliveryFee;

    @Column(name = "platform_fee", nullable = false, precision = 19, scale = 2)
    private BigDecimal platformFee;

    @Column(name = "restaurant_payout", nullable = false, precision = 19, scale = 2)
    private BigDecimal restaurantPayout;

    @Column(name = "driver_payout", nullable = false, precision = 19, scale = 2)
    private BigDecimal driverPayout;

    /** JPA requires a no-arg constructor. */
    protected PayoutBreakdown() {
    }

    private PayoutBreakdown(BigDecimal totalAmount, BigDecimal deliveryFee,
                            BigDecimal platformFee, BigDecimal restaurantPayout,
                            BigDecimal driverPayout) {
        this.totalAmount = totalAmount;
        this.deliveryFee = deliveryFee;
        this.platformFee = platformFee;
        this.restaurantPayout = restaurantPayout;
        this.driverPayout = driverPayout;
    }

    // ── Factory Method ──

    /**
     * Calculates payout splits based on commission rates.
     * <p>
     * Formula:
     * <pre>
     *   foodAmount         = totalAmount - deliveryFee
     *   restaurantCommission = foodAmount × restaurantRate
     *   driverCommission     = deliveryFee × driverRate
     *   restaurantPayout     = foodAmount - restaurantCommission
     *   driverPayout         = deliveryFee - driverCommission
     *   platformFee          = restaurantCommission + driverCommission
     * </pre>
     *
     * @param totalAmount              total order amount including delivery
     * @param deliveryFee              delivery fee portion
     * @param restaurantCommissionRate e.g. 0.15 = 15%
     * @param driverCommissionRate     e.g. 0.10 = 10%
     * @return calculated breakdown
     */
    public static PayoutBreakdown calculate(BigDecimal totalAmount, BigDecimal deliveryFee,
                                            double restaurantCommissionRate,
                                            double driverCommissionRate) {
        Objects.requireNonNull(totalAmount, "totalAmount must not be null");
        Objects.requireNonNull(deliveryFee, "deliveryFee must not be null");

        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Total amount must be greater than zero");
        }
        if (restaurantCommissionRate < 0 || restaurantCommissionRate >= 1) {
            throw new IllegalArgumentException("Restaurant commission rate must be in [0, 1)");
        }
        if (driverCommissionRate < 0 || driverCommissionRate >= 1) {
            throw new IllegalArgumentException("Driver commission rate must be in [0, 1)");
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
        BigDecimal platformFee = restaurantCommission
                .add(driverCommission).setScale(2, RoundingMode.HALF_UP);

        if (restaurantPayout.compareTo(BigDecimal.ZERO) < 0
                || driverPayout.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Calculated payout amounts cannot be negative. "
                            + "Restaurant: " + restaurantPayout + ", Driver: " + driverPayout);
        }

        return new PayoutBreakdown(totalAmount, deliveryFee, platformFee, restaurantPayout, driverPayout);
    }

    // ── Getters ──

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public BigDecimal getPlatformFee() {
        return platformFee;
    }

    public BigDecimal getRestaurantPayout() {
        return restaurantPayout;
    }

    public BigDecimal getDriverPayout() {
        return driverPayout;
    }

    // ── Equality & Hash (Value Object semantics) ──

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PayoutBreakdown other)) return false;
        return totalAmount.compareTo(other.totalAmount) == 0
                && deliveryFee.compareTo(other.deliveryFee) == 0
                && platformFee.compareTo(other.platformFee) == 0
                && restaurantPayout.compareTo(other.restaurantPayout) == 0
                && driverPayout.compareTo(other.driverPayout) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                totalAmount.stripTrailingZeros(),
                deliveryFee.stripTrailingZeros(),
                platformFee.stripTrailingZeros(),
                restaurantPayout.stripTrailingZeros(),
                driverPayout.stripTrailingZeros()
        );
    }

    @Override
    public String toString() {
        return String.format("PayoutBreakdown{total=%s, delivery=%s, platform=%s, restaurant=%s, driver=%s}",
                totalAmount, deliveryFee, platformFee, restaurantPayout, driverPayout);
    }
}
