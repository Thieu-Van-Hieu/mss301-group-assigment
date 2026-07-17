package mss301.se1911.group.assignment.paymentservices.infrastructure.persistence;

import mss301.se1911.group.assignment.paymentservices.domain.entity.PayoutRecord;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PayoutRecord.SettlementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for PayoutRecord.
 */
@Repository
public interface PayoutRecordJpaRepository
        extends JpaRepository<PayoutRecord, UUID>,
                JpaSpecificationExecutor<PayoutRecord> {

    Optional<PayoutRecord> findByOrderId(UUID orderId);

    boolean existsByOrderId(UUID orderId);

    List<PayoutRecord> findByStatusAndDriverSettlementStatusAndCreatedAtBefore(
            PayoutRecord.PayoutStatus status, SettlementStatus driverSettlementStatus, OffsetDateTime createdAt);

    List<PayoutRecord> findByStatusAndRestaurantSettlementStatusAndCreatedAtBefore(
            PayoutRecord.PayoutStatus status, SettlementStatus restaurantSettlementStatus, OffsetDateTime createdAt);
}
