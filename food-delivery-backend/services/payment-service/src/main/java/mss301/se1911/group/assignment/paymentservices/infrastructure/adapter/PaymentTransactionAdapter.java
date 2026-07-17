package mss301.se1911.group.assignment.paymentservices.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction;
import mss301.se1911.group.assignment.paymentservices.domain.repository.PaymentTransactionRepository;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction.PaymentStatus;
import mss301.se1911.group.assignment.paymentservices.infrastructure.persistence.PaymentTransactionJpaRepository;
import mss301.se1911.group.assignment.paymentservices.infrastructure.specification.PaymentTransactionSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter that implements the domain PaymentTransactionRepository port
 * by delegating to the Spring Data JPA repository and JPA Specifications.
 */
@Repository
@RequiredArgsConstructor
public class PaymentTransactionAdapter implements PaymentTransactionRepository {

    private final PaymentTransactionJpaRepository jpaRepository;

    @Override
    public PaymentTransaction save(PaymentTransaction transaction) {
        return jpaRepository.save(transaction);
    }

    @Override
    public Optional<PaymentTransaction> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<PaymentTransaction> findByIdForUpdate(UUID id) {
        return jpaRepository.findByIdForUpdate(id);
    }

    @Override
    public Optional<PaymentTransaction> findByOrderId(UUID orderId) {
        return jpaRepository.findByOrderId(orderId);
    }

    @Override
    public Optional<PaymentTransaction> findByGatewayTransId(String gatewayTransId) {
        return jpaRepository.findByGatewayTransId(gatewayTransId);
    }

    @Override
    public List<PaymentTransaction> findByStatusAndCreatedAtBefore(PaymentStatus status,
                                                                    OffsetDateTime cutoff) {
        Specification<PaymentTransaction> spec =
                PaymentTransactionSpecification.hasStatus(status)
                        .and(PaymentTransactionSpecification.createdBefore(cutoff));
        return jpaRepository.findAll(spec);
    }

    @Override
    public boolean existsByOrderIdAndStatusIn(UUID orderId, List<PaymentStatus> statuses) {
        Specification<PaymentTransaction> spec =
                PaymentTransactionSpecification.hasOrderId(orderId)
                        .and(PaymentTransactionSpecification.hasStatusIn(statuses));
        return jpaRepository.count(spec) > 0;
    }
}
