package mss301.se1911.group.assignment.paymentservices.domain.repository;

import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction.PaymentStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain port for PaymentTransaction persistence.
 * Infrastructure adapters provide the concrete implementation.
 */
public interface PaymentTransactionRepository {

    PaymentTransaction save(PaymentTransaction transaction);

    Optional<PaymentTransaction> findById(UUID id);

    Optional<PaymentTransaction> findByIdForUpdate(UUID id);

    Optional<PaymentTransaction> findByOrderId(UUID orderId);

    Optional<PaymentTransaction> findByGatewayTransId(String gatewayTransId);

    List<PaymentTransaction> findByStatusAndCreatedAtBefore(PaymentStatus status, OffsetDateTime cutoff);

    boolean existsByOrderIdAndStatusIn(UUID orderId, List<PaymentStatus> statuses);
}
