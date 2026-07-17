package mss301.se1911.group.assignment.paymentservices.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value Object representing a monetary amount with its currency.
 * <p>
 * Immutable — all mutating operations return a new {@code Money} instance.
 * Equality is determined by both {@code amount} and {@code currency}.
 * <p>
 * Business rules:
 * <ul>
 *   <li>Amount must be non-negative</li>
 *   <li>Currency must be a 3-letter ISO 4217 code</li>
 *   <li>Arithmetic operations between different currencies are disallowed</li>
 * </ul>
 */
@Embeddable
public final class Money {

    private static final String DEFAULT_CURRENCY = "VND";

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    /** JPA requires a no-arg constructor. */
    protected Money() {
    }

    private Money(BigDecimal amount, String currency) {
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(currency, "currency must not be null");

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be non-negative, got: " + amount);
        }
        if (currency.length() != 3) {
            throw new IllegalArgumentException("Currency must be a 3-letter ISO 4217 code, got: " + currency);
        }

        this.amount = amount;
        this.currency = currency.toUpperCase();
    }

    // ── Factory Methods ──

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }

    public static Money ofVnd(BigDecimal amount) {
        return new Money(amount, DEFAULT_CURRENCY);
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO, DEFAULT_CURRENCY);
    }

    public static Money zero(String currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    // ── Getters ──

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    // ── Arithmetic (returns new Money — immutable) ──

    public Money add(Money other) {
        guardSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        guardSameCurrency(other);
        BigDecimal result = this.amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Subtraction would result in negative amount: " + this.amount + " - " + other.amount);
        }
        return new Money(result, this.currency);
    }

    // ── Query Methods ──

    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isGreaterThan(Money other) {
        guardSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isLessThan(Money other) {
        guardSameCurrency(other);
        return this.amount.compareTo(other.amount) < 0;
    }

    // ── Equality & Hash (Value Object semantics) ──

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money other)) return false;
        return amount.compareTo(other.amount) == 0
                && currency.equals(other.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros(), currency);
    }

    @Override
    public String toString() {
        return amount.toPlainString() + " " + currency;
    }

    // ── Private Helpers ──

    private void guardSameCurrency(Money other) {
        Objects.requireNonNull(other, "other Money must not be null");
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                    "Cannot operate on Money with different currencies: "
                            + this.currency + " vs " + other.currency);
        }
    }
}
