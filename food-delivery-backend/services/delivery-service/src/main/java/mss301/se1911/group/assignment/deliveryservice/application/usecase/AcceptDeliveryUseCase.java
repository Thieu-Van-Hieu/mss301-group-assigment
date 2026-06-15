package mss301.se1911.group.assignment.deliveryservice.application.usecase;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.deliveryservice.domain.aggregate.DeliveryAggregate;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.DeliveryRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AcceptDeliveryUseCase {
    private final DeliveryRepository deliveryRepository;

    @Transactional
    public void execute(UUID deliveryId, UUID driverId) {
        DeliveryAggregate deliveryAggregate = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chuyến xe!"));

        // Gọi Aggregate xử lý logic nghiệp vụ
        deliveryAggregate.acceptByDriver(driverId);
        deliveryRepository.save(deliveryAggregate);
    }
}