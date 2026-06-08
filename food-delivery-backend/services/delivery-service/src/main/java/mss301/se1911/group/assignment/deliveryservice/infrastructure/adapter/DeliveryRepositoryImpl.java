package mss301.se1911.group.assignment.deliveryservice.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.deliveryservice.domain.aggregate.DeliveryAggregate;
import mss301.se1911.group.assignment.deliveryservice.domain.aggregate.DriverProfileAggregate;
import mss301.se1911.group.assignment.deliveryservice.domain.entity.DeliveryEntity;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.DeliveryRepository;
import mss301.se1911.group.assignment.deliveryservice.infrastructure.persistence.JpaDeliveryRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeliveryRepositoryImpl implements DeliveryRepository {

    private final JpaDeliveryRepository jpaDeliveryRepository;
    @Override
    public Optional<DeliveryAggregate> findDeliveryById(UUID id) {
        DeliveryEntity deliveryEntity = jpaDeliveryRepository.findById(id).orElse(null);
        if (deliveryEntity == null) {
            return Optional.empty();
        }
        return Optional.of(new DeliveryAggregate(deliveryEntity));
    }

    @Override
    public void save(DeliveryAggregate deliveryAggregate) {

    }
}
