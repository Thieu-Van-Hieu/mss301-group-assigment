package mss301.se1911.group.assignment.deliveryservice.application.usecase;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.deliveryservice.domain.aggregate.DeliveryAggregate;
import mss301.se1911.group.assignment.deliveryservice.domain.aggregate.DriverProfileAggregate;
import mss301.se1911.group.assignment.deliveryservice.domain.enums.DeliveryStatus;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.DeliveryRepository;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.DriverProfileRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateDeliveryProgressUseCase {
    private final DeliveryRepository deliveryRepository;
    private final DriverProfileRepository driverProfileRepository; // Để tương tác với ví tài xế

    @Transactional
    public void execute(UUID deliveryId, UUID driverId, DeliveryStatus newStatus) {
        DeliveryAggregate deliveryAggregate = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chuyến xe!"));

        // Đảm bảo chỉ tài xế đang giữ đơn mới được quyền cập nhật
        if (!deliveryAggregate.getRootEntity().getDriver().getDriverId().equals(driverId)) {
            throw new IllegalStateException("Bạn không có quyền cập nhật chuyến xe này!");
        }

        // Định tuyến hành vi thay đổi trạng thái
        switch (newStatus) {
            case ARRIVED_AT_RESTAURANT -> deliveryAggregate.arriveAtRestaurant();
            case PICKED_UP -> deliveryAggregate.pickupOrder();
            case DELIVERED -> {
                deliveryAggregate.deliverSuccess();

                // --- XỬ LÝ KẾT THÚC CHUYẾN: CỘNG TIỀN VÀO VÍ TÀI XẾ ---
                DriverProfileAggregate driverAggregate = driverProfileRepository.findById(driverId)
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài xế!"));

                BigDecimal fee = deliveryAggregate.getRootEntity().getDeliveryFee();

                // Giả định bạn có hàm addFunds() trong DriverAggregate
                // driverAggregate.addFunds(fee);

                // Tạm thời set trực tiếp nếu chưa có hàm addFunds
                BigDecimal currentBalance = driverAggregate.getRootEntity().getWalletBalance();
                driverAggregate.getRootEntity().setWalletBalance(currentBalance.add(fee));

                driverProfileRepository.save(driverAggregate);
                // --------------------------------------------------------

                // [TODO]: Bắn Event Kafka báo cho Order-Service biết đơn đã hoàn thành
            }
            default -> throw new IllegalArgumentException("Trạng thái cập nhật không hợp lệ!");
        }

        deliveryRepository.save(deliveryAggregate);
    }
}