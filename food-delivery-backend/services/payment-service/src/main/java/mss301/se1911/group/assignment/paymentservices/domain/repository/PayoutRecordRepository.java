package mss301.se1911.group.assignment.paymentservices.domain.repository;

import mss301.se1911.group.assignment.paymentservices.domain.entity.PayoutRecord;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PayoutRecord.SettlementStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain port for PayoutRecord persistence.
 * Infrastructure adapters provide the concrete implementation.
 */
public interface PayoutRecordRepository {

    PayoutRecord save(PayoutRecord record);

    Optional<PayoutRecord> findByOrderId(UUID orderId);

    boolean existsByOrderId(UUID orderId);

    List<PayoutRecord> findByStatusAndDriverSettlementStatusAndCreatedAtBefore(
            PayoutRecord.PayoutStatus status, SettlementStatus driverSettlementStatus, OffsetDateTime createdAt);

    List<PayoutRecord> findByStatusAndRestaurantSettlementStatusAndCreatedAtBefore(
            PayoutRecord.PayoutStatus status, SettlementStatus restaurantSettlementStatus, OffsetDateTime createdAt);
}
