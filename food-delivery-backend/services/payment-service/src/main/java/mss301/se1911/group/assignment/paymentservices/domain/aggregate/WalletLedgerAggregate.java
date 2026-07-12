package mss301.se1911.group.assignment.paymentservices.domain.aggregate;

import lombok.Getter;
import mss301.se1911.group.assignment.paymentservices.domain.entity.WalletLedger;
import mss301.se1911.group.assignment.paymentservices.domain.exception.PaymentProcessingException;
import mss301.se1911.group.assignment.paymentservices.domain.entity.WalletLedger.LedgerEntryType;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate for WalletLedger (immutable, append-only).
 * Acts as a factory and validator — ensures data integrity constraints
 * (non-null fields, positive amounts, balance consistency) before the
 * WalletLedger entity is constructed.
 */
@Getter
public class WalletLedgerAggregate {

    private final WalletLedger ledger;

    private WalletLedgerAggregate(WalletLedger ledger) {
        this.ledger = ledger;
    }

    /**
     * Wraps an existing ledger entry loaded from persistence.
     */
    public static WalletLedgerAggregate from(WalletLedger ledger) {
        Objects.requireNonNull(ledger, "WalletLedger must not be null");
        return new WalletLedgerAggregate(ledger);
    }

    /**
     * Factory: create and validate a new ledger entry.
     * Verifies that balanceAfter is consistent with balanceBefore ± amount
     * based on the entry type.
     */
    public static WalletLedgerAggregate create(UUID walletId, UUID transactionRefId,
            LedgerEntryType entryType, BigDecimal amount,
            BigDecimal balanceBefore, BigDecimal balanceAfter,
            String description) {
        Objects.requireNonNull(walletId, "walletId must not be null");
        Objects.requireNonNull(transactionRefId, "transactionRefId must not be null");
        Objects.requireNonNull(entryType, "entryType must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(balanceBefore, "balanceBefore must not be null");
        Objects.requireNonNull(balanceAfter, "balanceAfter must not be null");
        Objects.requireNonNull(description, "description must not be null");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentProcessingException("Ledger entry amount must be greater than zero");
        }

        // Validate balance consistency
        BigDecimal expectedAfter = switch (entryType) {
            case CREDIT -> balanceBefore.add(amount);
            case DEBIT -> balanceBefore.subtract(amount);
        };

        if (expectedAfter.compareTo(balanceAfter) != 0) {
            throw new PaymentProcessingException(
                    String.format("Balance inconsistency for %s entry: expected %s but got %s",
                            entryType, expectedAfter, balanceAfter));
        }

        WalletLedger ledger = WalletLedger.builder()
                .walletId(walletId)
                .transactionRefId(transactionRefId)
                .entryType(entryType)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .description(description)
                .build();

        return new WalletLedgerAggregate(ledger);
    }
}
