package mss301.se1911.group.assignment.paymentservices.domain.aggregate;

import lombok.Getter;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction;
import mss301.se1911.group.assignment.paymentservices.domain.exception.PaymentProcessingException;
import mss301.se1911.group.assignment.paymentservices.domain.exception.SePayValidationException;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction.PaymentMethod;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction.PaymentGateway;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction.PaymentStatus;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction.TransactionType;
import mss301.se1911.group.assignment.paymentservices.domain.vo.Money;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate root for PaymentTransaction.
 * Encapsulates all payment state-machine transitions and business validations
 * before data is persisted to the entity.
 */
@Getter
public class PaymentTransactionAggregate {

    private final PaymentTransaction transaction;

    private PaymentTransactionAggregate(PaymentTransaction transaction) {
        this.transaction = transaction;
    }

    /**
     * Wraps an existing entity loaded from persistence.
     */
    public static PaymentTransactionAggregate from(PaymentTransaction transaction) {
        Objects.requireNonNull(transaction, "PaymentTransaction must not be null");
        return new PaymentTransactionAggregate(transaction);
    }

    /**
     * Factory: create a brand-new payment in PENDING status.
     */
    public static PaymentTransactionAggregate initiate(UUID orderId, UUID customerId,
                                                       BigDecimal amount, PaymentMethod paymentMethod, PaymentGateway paymentGateway) {
        Objects.requireNonNull(orderId, "orderId must not be null");
        Objects.requireNonNull(customerId, "customerId must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(paymentMethod, "paymentMethod must not be null");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentProcessingException("Payment amount must be greater than zero");
        }

        PaymentTransaction tx = PaymentTransaction.builder()
                .orderId(orderId)
                .customerId(customerId)
                .money(Money.ofVnd(amount))
                .paymentMethod(paymentMethod)
                .paymentGateway(paymentGateway)
                .status(PaymentStatus.PENDING)
                .build();

        return new PaymentTransactionAggregate(tx);
    }

    /**
     * Factory: create a top-up transaction (no orderId, transactionType = WALLET_TOPUP).
     * The gatewayMethod specifies which payment gateway to use for funding (SEPAY, PAYOS).
     */
    public static PaymentTransactionAggregate initiateTopUp(UUID customerId,
                                                            BigDecimal amount,
                                                            PaymentGateway gatewayMethod) {
        Objects.requireNonNull(customerId, "customerId must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(gatewayMethod, "gatewayMethod must not be null");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentProcessingException("Top-up amount must be greater than zero");
        }

        PaymentTransaction tx = PaymentTransaction.builder()
                .orderId(null)
                .customerId(customerId)
                .money(Money.ofVnd(amount))
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentGateway(gatewayMethod)
                .status(PaymentStatus.PENDING)
                .transactionType(TransactionType.WALLET_TOPUP)
                .build();

        return new PaymentTransactionAggregate(tx);
    }

    // ── State Transition Methods ──

    /**
     * PENDING | PROCESSING → PROCESSING (idempotent).
     */
    public void markProcessing() {
        if (transaction.getStatus() != PaymentStatus.PENDING
                && transaction.getStatus() != PaymentStatus.PROCESSING) {
            throw new PaymentProcessingException(
                    "Transaction " + transaction.getId()
                            + " cannot be processed in status " + transaction.getStatus());
        }
        transaction.setStatus(PaymentStatus.PROCESSING);
    }

    /**
     * PENDING | PROCESSING → PAID via payment gateway.
     * Idempotent: returns silently if already PAID.
     */
    public void markPaidByGateway(String gatewayTransId, String gatewayResponse) {
        if (transaction.getStatus() == PaymentStatus.PAID) {
            return; // idempotent
        }
        guardCanTransitionToPaid();
        transaction.setStatus(PaymentStatus.PAID);
        transaction.setGatewayTransId(gatewayTransId);
        transaction.setGatewayResponse(gatewayResponse);
        transaction.setPaidAt(OffsetDateTime.now());
    }

    /**
     * PENDING → PAID for COD payments.
     * Idempotent: returns silently if already PAID.
     */
    public void confirmCodPayment() {
        if (transaction.getPaymentMethod() != PaymentMethod.COD) {
            throw new PaymentProcessingException(
                    "Order " + transaction.getOrderId() + " is not using COD");
        }
        if (transaction.getStatus() == PaymentStatus.PAID) {
            return; // idempotent
        }
        if (transaction.getStatus() != PaymentStatus.PENDING) {
            throw new PaymentProcessingException(
                    "Cannot confirm COD for order " + transaction.getOrderId()
                            + " in status " + transaction.getStatus());
        }
        transaction.setStatus(PaymentStatus.PAID);
        transaction.setPaidAt(OffsetDateTime.now());
    }

    /**
     * PENDING → PAID for WALLET payments (instant debit from wallet balance).
     * Idempotent: returns silently if already PAID.
     */
    public void confirmWalletPayment() {
        if (transaction.getPaymentMethod() != PaymentMethod.WALLET) {
            throw new PaymentProcessingException(
                    "Order " + transaction.getOrderId() + " is not using WALLET");
        }
        if (transaction.getStatus() == PaymentStatus.PAID) {
            return; // idempotent
        }
        if (transaction.getStatus() != PaymentStatus.PENDING) {
            throw new PaymentProcessingException(
                    "Cannot confirm WALLET payment for order " + transaction.getOrderId()
                            + " in status " + transaction.getStatus());
        }
        transaction.setStatus(PaymentStatus.PAID);
        transaction.setPaidAt(OffsetDateTime.now());
    }

    /**
     * Marks the transaction as FAILED with a reason.
     */
    public void markFailed(String reason) {
        transaction.setStatus(PaymentStatus.FAILED);
        transaction.setFailureReason(reason);
    }

    /**
     * PAID → REFUNDED.
     * Idempotent: returns silently if already REFUNDED.
     */
    public void markRefunded() {
        if (transaction.getStatus() == PaymentStatus.REFUNDED) {
            return; // idempotent
        }
        if (transaction.getStatus() != PaymentStatus.PAID) {
            throw new PaymentProcessingException(
                    "Cannot refund order " + transaction.getOrderId()
                            + " in status " + transaction.getStatus());
        }
        transaction.setStatus(PaymentStatus.REFUNDED);
    }

    // ── Validation Methods ──

    /**
     * Update the actual payment amount. Used when a top-up receives more than requested.
     */
    public void updateAmount(BigDecimal newAmount) {
        if (newAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentProcessingException("Payment amount must be greater than zero");
        }
        transaction.setMoney(Money.of(newAmount, transaction.getMoney().getCurrency()));
    }

    /**
     * Validates that the payment method is SePay.
     */
    public void validateSePayMethod() {
        if (transaction.getPaymentGateway() != PaymentGateway.SEPAY) {
            throw new PaymentProcessingException(
                    "Transaction " + transaction.getId() + " is not a SePay payment");
        }
    }

    /**
     * Validates the SePay webhook amount against the stored amount.
     * <p>
     * If validation fails, the transaction is marked FAILED before throwing.
     */
    public void validateSePayAmount(BigDecimal transferAmount) {
        if (transferAmount == null) {
            markFailed("Missing transferAmount in SePay webhook");
            throw new SePayValidationException("Missing transferAmount in SePay webhook");
        }
        BigDecimal expectedAmount = transaction.getMoney().getAmount();
        
        // Allow overpayment, but fail if transferAmount is less than expected
        if (transferAmount.compareTo(expectedAmount) < 0) {
            String reason = String.format(
                    "Amount mismatch: SePay received %s but expected at least %s",
                    transferAmount, expectedAmount);
            markFailed(reason);
            throw new SePayValidationException(reason);
        }
    }




    // ── Query Methods ──

    public boolean isPaid() {
        return transaction.getStatus() == PaymentStatus.PAID;
    }

    public boolean isSePay() {
        return transaction.getPaymentGateway() == PaymentGateway.SEPAY;
    }


    public boolean canProcess() {
        return transaction.getStatus() == PaymentStatus.PENDING
                || transaction.getStatus() == PaymentStatus.PROCESSING;
    }

    public boolean isWallet() {
        return transaction.getPaymentMethod() == PaymentMethod.WALLET;
    }

    public boolean isTopUp() {
        return transaction.getTransactionType() == TransactionType.WALLET_TOPUP;
    }

    // ── Private Helpers ──

    private void guardCanTransitionToPaid() {
        if (transaction.getStatus() != PaymentStatus.PENDING
                && transaction.getStatus() != PaymentStatus.PROCESSING) {
            throw new PaymentProcessingException(
                    "Transaction " + transaction.getId()
                            + " cannot be marked PAID in status " + transaction.getStatus());
        }
    }
}
