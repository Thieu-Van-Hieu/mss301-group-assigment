package mss301.se1911.group.assignment.paymentservices.infrastructure.persistence;

import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for PaymentTransaction.
 * Supports derived queries and Specification-based dynamic queries.
 */
@Repository
public interface PaymentTransactionJpaRepository
        extends JpaRepository<PaymentTransaction, UUID>,
                JpaSpecificationExecutor<PaymentTransaction> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PaymentTransaction p WHERE p.id = :id")
    Optional<PaymentTransaction> findByIdForUpdate(@Param("id") UUID id);

    Optional<PaymentTransaction> findByOrderId(UUID orderId);

    Optional<PaymentTransaction> findByGatewayTransId(String gatewayTransId);
}
