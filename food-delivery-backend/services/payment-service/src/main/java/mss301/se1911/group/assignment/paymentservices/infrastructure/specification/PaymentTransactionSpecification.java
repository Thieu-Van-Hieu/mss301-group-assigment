package mss301.se1911.group.assignment.paymentservices.infrastructure.specification;

import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction.PaymentStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * JPA Specification factory for PaymentTransaction criteria queries.
 * Enables composable, type-safe dynamic queries via JpaSpecificationExecutor.
 */
public final class PaymentTransactionSpecification {

    private PaymentTransactionSpecification() {
        // utility class — no instantiation
    }

    public static Specification<PaymentTransaction> hasStatus(PaymentStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<PaymentTransaction> hasStatusIn(List<PaymentStatus> statuses) {
        return (root, query, cb) -> root.get("status").in(statuses);
    }

    public static Specification<PaymentTransaction> createdBefore(OffsetDateTime cutoff) {
        return (root, query, cb) -> cb.lessThan(root.get("createdAt"), cutoff);
    }

    public static Specification<PaymentTransaction> hasOrderId(UUID orderId) {
        return (root, query, cb) -> cb.equal(root.get("orderId"), orderId);
    }
}
