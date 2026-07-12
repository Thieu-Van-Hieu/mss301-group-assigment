package mss301.se1911.group.assignment.paymentservices.domain.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mss301.se1911.group.assignment.paymentservices.domain.vo.Money;
import mss301.se1911.group.assignment.paymentservices.domain.vo.Owner;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "wallets")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Embedded
    private Owner owner;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "balance", nullable = false, precision = 19, scale = 2))
    @AttributeOverride(name = "currency", column = @Column(name = "currency", nullable = false, length = 3))
    @Builder.Default
    private Money balance = Money.zero();

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "pending_balance", nullable = false, precision = 19, scale = 2))
    @AttributeOverride(name = "currency", column = @Column(name = "currency", nullable = false, length = 3, insertable = false, updatable = false))
    @Builder.Default
    private Money pendingBalance = Money.zero();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private WalletStatus status = WalletStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    // ──────────────────────────────────────────────
    // Status enum — lifecycle state of this entity,
    // NOT a Value Object.
    // ──────────────────────────────────────────────

    /**
     * Lifecycle status of a wallet.
     */
    public enum WalletStatus {
        ACTIVE,
        FROZEN,
        CLOSED
    }
}
