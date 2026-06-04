package mss301.se1911.group.assignment.paymentservices.domain.aggregate;

import lombok.Getter;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction;
import mss301.se1911.group.assignment.paymentservices.domain.exception.PaymentProcessingException;
import mss301.se1911.group.assignment.paymentservices.domain.exception.VnPayValidationException;
import mss301.se1911.group.assignment.paymentservices.domain.vo.PaymentMethod;
import mss301.se1911.group.assignment.paymentservices.domain.vo.PaymentStatus;

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
                                                       BigDecimal amount, PaymentMethod paymentMethod) {
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
                .amount(amount)
                .paymentMethod(paymentMethod)
                .status(PaymentStatus.PENDING)
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
     * Validates that the payment method is VNPay.
     */
    public void validateVnPayMethod() {
        if (transaction.getPaymentMethod() != PaymentMethod.VNPAY) {
            throw new PaymentProcessingException(
                    "Transaction " + transaction.getId() + " is not a VNPay payment");
        }
    }

    /**
     * Validates the VNPay callback amount against the stored amount.
     * VNPay sends amount × 100 (e.g., 100,000 VND → vnp_Amount = "10000000").
     * <p>
     * If validation fails, the transaction is marked FAILED before throwing.
     */
    public void validateVnPayAmount(String vnpAmountStr) {
        if (vnpAmountStr == null) {
            markFailed("Missing vnp_Amount in VNPay callback");
            throw new VnPayValidationException("Missing vnp_Amount in VNPay callback");
        }
        try {
            long vnpAmount = Long.parseLong(vnpAmountStr);
            long expectedAmount = transaction.getAmount().longValue() * 100;
            if (vnpAmount != expectedAmount) {
                String reason = String.format(
                        "Amount mismatch: VNPay returned %d but expected %d (order amount: %s)",
                        vnpAmount, expectedAmount, transaction.getAmount());
                markFailed(reason);
                throw new VnPayValidationException(reason);
            }
        } catch (NumberFormatException e) {
            String reason = "Invalid vnp_Amount format: " + vnpAmountStr;
            markFailed(reason);
            throw new VnPayValidationException(reason);
        }
    }

    // ── Query Methods ──

    public boolean isPaid() {
        return transaction.getStatus() == PaymentStatus.PAID;
    }

    public boolean isVnPay() {
        return transaction.getPaymentMethod() == PaymentMethod.VNPAY;
    }

    public boolean canProcess() {
        return transaction.getStatus() == PaymentStatus.PENDING
                || transaction.getStatus() == PaymentStatus.PROCESSING;
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
