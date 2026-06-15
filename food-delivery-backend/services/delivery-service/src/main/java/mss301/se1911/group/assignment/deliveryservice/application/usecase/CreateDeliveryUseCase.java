package mss301.se1911.group.assignment.deliveryservice.application.usecase;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.deliveryservice.application.command.CreateDeliveryCommand;
import mss301.se1911.group.assignment.deliveryservice.domain.aggregate.DeliveryAggregate;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.DeliveryRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateDeliveryUseCase {

    private final DeliveryRepository deliveryRepository;

    @Transactional
    public void execute(CreateDeliveryCommand command) {
        // Khởi tạo Aggregate Root thông qua Factory Method
        DeliveryAggregate deliveryAggregate = DeliveryAggregate.createNewDelivery(
                command.orderId(),
                command.pickupAddress(), command.pickupLat(), command.pickupLng(),
                command.dropoffAddress(), command.dropoffLat(), command.dropoffLng(),
                command.codAmount(), command.deliveryFee()
        );

        // Lưu trạng thái READY_TO_MATCH xuống DB
        deliveryRepository.save(deliveryAggregate);

        // [TODO - Tương lai]: Gọi sang Redis Cache để đẩy đơn này vào danh sách "Bể đơn chờ gán" cho Matching Engine quét.
    }
}
