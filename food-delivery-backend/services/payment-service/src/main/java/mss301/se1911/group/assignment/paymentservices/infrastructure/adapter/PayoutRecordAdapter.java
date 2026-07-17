package mss301.se1911.group.assignment.paymentservices.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PayoutRecord;
import mss301.se1911.group.assignment.paymentservices.domain.repository.PayoutRecordRepository;
import mss301.se1911.group.assignment.paymentservices.infrastructure.persistence.PayoutRecordJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PayoutRecord.SettlementStatus;

/**
 * Adapter that implements the domain PayoutRecordRepository port.
 */
@Repository
@RequiredArgsConstructor
public class PayoutRecordAdapter implements PayoutRecordRepository {

    private final PayoutRecordJpaRepository jpaRepository;

    @Override
    public PayoutRecord save(PayoutRecord record) {
        return jpaRepository.save(record);
    }

    @Override
    public Optional<PayoutRecord> findByOrderId(UUID orderId) {
        return jpaRepository.findByOrderId(orderId);
    }

    @Override
    public boolean existsByOrderId(UUID orderId) {
        return jpaRepository.existsByOrderId(orderId);
    }

    @Override
    public List<PayoutRecord> findByStatusAndDriverSettlementStatusAndCreatedAtBefore(
            PayoutRecord.PayoutStatus status, SettlementStatus driverSettlementStatus, OffsetDateTime createdAt) {
        return jpaRepository.findByStatusAndDriverSettlementStatusAndCreatedAtBefore(status, driverSettlementStatus, createdAt);
    }

    @Override
    public List<PayoutRecord> findByStatusAndRestaurantSettlementStatusAndCreatedAtBefore(
            PayoutRecord.PayoutStatus status, SettlementStatus restaurantSettlementStatus, OffsetDateTime createdAt) {
        return jpaRepository.findByStatusAndRestaurantSettlementStatusAndCreatedAtBefore(status, restaurantSettlementStatus, createdAt);
    }
}
