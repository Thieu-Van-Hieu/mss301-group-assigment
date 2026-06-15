package mss301.se1911.group.assignment.deliveryservice.application.usecase;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.deliveryservice.domain.aggregate.DeliveryAggregate;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.DeliveryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CancelDeliveryUseCases {

    private final DeliveryRepository deliveryRepository;

    @Transactional
    public void cancelByCustomer(UUID deliveryId, String reason) {
        DeliveryAggregate deliveryAggregate = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chuyến xe!"));

        // Logic Aggregate: Chỉ cho phép hủy nếu xế chưa lấy hàng
        deliveryAggregate.cancelByCustomer(reason);
        deliveryRepository.save(deliveryAggregate);

        // [TODO]: Bắn API Feign hoặc Kafka báo cho Order-Service hủy đơn, hoàn tiền.
    }

    @Transactional
    public void cancelByDriver(UUID deliveryId, UUID driverId, String reason) {
        DeliveryAggregate deliveryAggregate = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chuyến xe!"));

        if (!deliveryAggregate.getRootEntity().getDriver().getDriverId().equals(driverId)) {
            throw new IllegalStateException("Bạn không có quyền báo sự cố cho chuyến xe này!");
        }

        // Logic Aggregate: Tài xế báo sự cố (xe hỏng, bom hàng)
        deliveryAggregate.cancelByDriver(reason);
        deliveryRepository.save(deliveryAggregate);

        // [TODO]: Bắn Event "DELIVERY_FAILED" cho Order-Service để CSKH gọi điện hỗ trợ đền bù.
    }
}
