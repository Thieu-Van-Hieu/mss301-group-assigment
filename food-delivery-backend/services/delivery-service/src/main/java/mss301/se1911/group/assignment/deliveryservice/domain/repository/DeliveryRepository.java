package mss301.se1911.group.assignment.deliveryservice.domain.repository;

import mss301.se1911.group.assignment.deliveryservice.domain.aggregate.DeliveryAggregate;
import mss301.se1911.group.assignment.deliveryservice.domain.aggregate.DriverProfileAggregate;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository {

    Optional<DeliveryAggregate> findDeliveryById(UUID id);
    void save(DeliveryAggregate deliveryAggregate);
}
