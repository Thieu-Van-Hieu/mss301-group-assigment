package mss301.se1911.group.assignment.deliveryservice.infrastructure.persistence;

import mss301.se1911.group.assignment.deliveryservice.domain.entity.DeliveryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaDeliveryRepository extends JpaRepository<DeliveryEntity, UUID> {
}
