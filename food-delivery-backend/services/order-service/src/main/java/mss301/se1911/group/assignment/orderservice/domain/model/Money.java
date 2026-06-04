package mss301.se1911.group.assignment.orderservice.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import mss301.se1911.group.assignment.orderservice.domain.exception.OrderDomainException;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@EqualsAndHashCode
@ToString
public class Money {
    private final BigDecimal amount;
    private final String currency;

    public static final Money ZERO_VND = new Money(BigDecimal.ZERO, "VND");

    public Money(BigDecimal amount, String currency) {
        if (amount == null) {
            throw new OrderDomainException("Money amount cannot be null");
        }
        if (currency == null || currency.trim().isEmpty()) {
            throw new OrderDomainException("Money currency cannot be empty");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new OrderDomainException("Money amount cannot be negative");
        }
        this.amount = amount.setScale(2, RoundingMode.HALF_EVEN);
        this.currency = currency.toUpperCase();
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }

    public static Money of(double amount, String currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }

    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public Money multiply(int multiplier) {
        if (multiplier < 0) {
            throw new OrderDomainException("Multiplier cannot be negative");
        }
        return new Money(this.amount.multiply(BigDecimal.valueOf(multiplier)), this.currency);
    }

    public boolean isGreaterThanOrEqual(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) >= 0;
    }

    public boolean isGreaterThan(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    private void validateSameCurrency(Money other) {
        if (other == null || !this.currency.equals(other.currency)) {
            throw new OrderDomainException("Currencies must match: " + this.currency + " vs " + (other != null ? other.currency : "null"));
        }
    }
}
