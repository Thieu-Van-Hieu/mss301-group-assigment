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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mss301.se1911.group.assignment.paymentservices.domain.vo.Money;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "amount", nullable = false, precision = 19, scale = 2))
    @AttributeOverride(name = "currency", column = @Column(name = "currency", nullable = false, length = 3))
    @Builder.Default
    private Money money = Money.zero();

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_gateway", length = 20)
    private PaymentGateway paymentGateway;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    @Builder.Default
    private TransactionType transactionType = TransactionType.ORDER_PAYMENT;

    @Column(name = "gateway_trans_id", length = 255)
    private String gatewayTransId;

    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "paid_at")
    private OffsetDateTime paidAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // ──────────────────────────────────────────────
    // Status & Method enums — tightly coupled to this entity,
    // NOT Value Objects (no multi-field immutability / behavior).
    // ──────────────────────────────────────────────

    /**
     * Payment method supported by the platform.
     */
    public enum PaymentMethod {
        WALLET,
        BANK_TRANSFER,
        COD
    }

    /**
     * Specific payment gateway used when method is BANK_TRANSFER.
     */
    public enum PaymentGateway {
        SEPAY
    }

    /**
     * Distinguishes order payments from wallet top-up transactions.
     */
    public enum TransactionType {
        ORDER_PAYMENT,
        WALLET_TOPUP
    }

    /**
     * Lifecycle status of a payment transaction.
     * Valid transitions:
     * PENDING → PROCESSING → PAID | FAILED
     * PAID → REFUNDED
     * any → CANCELLED
     */
    public enum PaymentStatus {
        PENDING,
        PROCESSING,
        PAID,
        FAILED,
        CANCELLED,
        REFUNDED
    }
}
