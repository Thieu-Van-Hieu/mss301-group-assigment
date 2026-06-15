package mss301.se1911.group.assignment.deliveryservice.infrastructure.persistence;

import io.lettuce.core.dynamic.annotation.Param;
import mss301.se1911.group.assignment.deliveryservice.domain.entity.DeliveryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface JpaDeliveryRepository extends JpaRepository<DeliveryEntity, UUID>,
        JpaSpecificationExecutor<DeliveryEntity> {

    // Tìm đơn hàng đang xử lý (không phải READY_TO_MATCH, cũng không phải trạng thái kết thúc)
    @Query("SELECT d FROM DeliveryEntity d WHERE d.driver.driverId = :driverId AND d.status NOT IN ('DELIVERED', 'FAILED', 'READY_TO_MATCH')")
    Optional<DeliveryEntity> findActiveDeliveryByDriverId(@Param("driverId") UUID driverId);
}