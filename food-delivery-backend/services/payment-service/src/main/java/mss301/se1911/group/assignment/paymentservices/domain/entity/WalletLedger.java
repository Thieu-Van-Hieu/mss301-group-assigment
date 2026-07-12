package mss301.se1911.group.assignment.paymentservices.domain.entity;

import jakarta.persistence.Column;
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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Immutable ledger entry for double-entry bookkeeping.
 * APPEND-ONLY — UPDATE and DELETE are blocked by DB triggers.
 */
@Entity
@Table(name = "wallet_ledgers")
@Immutable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "wallet_id", nullable = false)
    private UUID walletId;

    @Column(name = "transaction_ref_id", nullable = false)
    private UUID transactionRefId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false, length = 10)
    private LedgerEntryType entryType;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance_before", nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceBefore;

    @Column(name = "balance_after", nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    // ──────────────────────────────────────────────
    // Entry type enum — specific to ledger entries,
    // NOT a Value Object.
    // ──────────────────────────────────────────────

    /**
     * Double-entry bookkeeping entry types.
     * DEBIT  = money going out of a wallet.
     * CREDIT = money coming into a wallet.
     */
    public enum LedgerEntryType {
        DEBIT,
        CREDIT
    }
}

