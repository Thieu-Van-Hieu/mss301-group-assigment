package mss301.se1911.group.assignment.customerservice.infrastructure.persistence;

import mss301.se1911.group.assignment.customerservice.domain.entity.OrderHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface JpaOrderHistoryRepository extends JpaRepository<OrderHistoryEntity, UUID>,
        JpaSpecificationExecutor<OrderHistoryEntity> {

    Optional<OrderHistoryEntity> findByOrderIdAndCustomerId(UUID orderId, UUID customerId);
}
