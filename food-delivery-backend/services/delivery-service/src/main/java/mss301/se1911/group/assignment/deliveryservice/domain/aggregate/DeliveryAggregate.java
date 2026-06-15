package mss301.se1911.group.assignment.deliveryservice.domain.aggregate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.deliveryservice.domain.entity.DeliveryEntity;
import mss301.se1911.group.assignment.deliveryservice.domain.entity.DriverProfileEntity;
import mss301.se1911.group.assignment.deliveryservice.domain.enums.DeliveryStatus;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class DeliveryAggregate {

    private final DeliveryEntity rootEntity;

    /**
     * 1. CREATE: Factory Method để khởi tạo chuyến giao hàng mới từ Order-Service
     */
    public static DeliveryAggregate createNewDelivery(
            UUID orderId, String pickupAddress, BigDecimal pickupLat, BigDecimal pickupLng,
            String dropoffAddress, BigDecimal dropoffLat, BigDecimal dropoffLng,
            BigDecimal codAmount, BigDecimal deliveryFee) {

        DeliveryEntity entity = DeliveryEntity.builder()
                .id(UUID.randomUUID()) // Hoặc dùng ID từ hệ thống tạo ID phân tán
                .orderId(orderId)
                .pickupAddress(pickupAddress)
                .pickupLat(pickupLat)
                .pickupLng(pickupLng)
                .dropoffAddress(dropoffAddress)
                .dropoffLat(dropoffLat)
                .dropoffLng(dropoffLng)
                .codAmount(codAmount)
                .deliveryFee(deliveryFee)
                .status(DeliveryStatus.READY_TO_MATCH) // Vừa tạo ra là sẵn sàng để quăng vào bể cho tài xế
                .createdAt(ZonedDateTime.now())
                .build();

        return new DeliveryAggregate(entity);
    }

    /**
     * 2. HỆ THỐNG GÁN ĐƠN (Matching Engine chỉ định 1 tài xế)
     */
    public void assignDriver(DriverProfileEntity driverEntity) {
        if (this.rootEntity.getStatus() != DeliveryStatus.READY_TO_MATCH ) {
            throw new IllegalStateException("Đơn hàng không ở trạng thái sẵn sàng để điều phối!");
        }

        this.rootEntity.setDriver(driverEntity);
        this.rootEntity.setStatus(DeliveryStatus.ASSIGNED);
    }

    /**
     * 3. XỬ LÝ TỪ CHỐI / TIMEOUT: Gỡ tài xế ra để trả lại bể đơn
     * (Lưu ý: Việc thêm driverId vào Redis Blacklist sẽ do UseCase bên ngoài đảm nhiệm)
     */
    public void timeoutOrRejectAssignment() {
        if (this.rootEntity.getStatus() != DeliveryStatus.ASSIGNED) {
            throw new IllegalStateException("Đơn hàng không ở trạng thái đang gán để có thể hủy/từ chối!");
        }
        this.rootEntity.setDriver(null);
        this.rootEntity.setStatus(DeliveryStatus.READY_TO_MATCH);
    }

    /**
     * Trạng thái 3: Tài xế bấm CHẤP NHẬN đơn hàng
     */
    public void acceptByDriver(UUID driverId) {
        if (this.rootEntity.getStatus() != DeliveryStatus.ASSIGNED) {
            throw new IllegalStateException("Đơn hàng đã được nhận bởi người khác hoặc đã quá thời gian phản hồi!");
        }
        if (!this.rootEntity.getDriver().getDriverId().equals(driverId)) {
            throw new IllegalArgumentException("Mã tài xế không trùng khớp với tài xế được gán đơn!");
        }

        this.rootEntity.setStatus(DeliveryStatus.ACCEPTED);
    }

    /**
     * Trạng thái 4: Tài xế đã ĐẾN QUÁN ĂN (Arrived at Restaurant)
     */
    public void arriveAtRestaurant() {
        if (this.rootEntity.getStatus() != DeliveryStatus.ACCEPTED) {
            throw new IllegalStateException("Chỉ có thể cập nhật 'Đã đến quán' sau khi đã chấp nhận đơn!");
        }
        this.rootEntity.setStatus(DeliveryStatus.ARRIVED_AT_RESTAURANT);
    }

    /**
     * Trạng thái 5: Tài xế ĐÃ LẤY HÀNG thành công và bắt đầu đi ship
     */
    public void pickupOrder() {
        if (this.rootEntity.getStatus() != DeliveryStatus.ARRIVED_AT_RESTAURANT) {
            throw new IllegalStateException("Tài xế phải đến quán ăn trước khi xác nhận đã lấy hàng!");
        }
        this.rootEntity.setStatus(DeliveryStatus.PICKED_UP);
        this.rootEntity.setPickupTime(ZonedDateTime.now());
    }

    /**
     * Trạng thái 6: ĐÃ GIAO HÀNG thành công (Kết thúc chuyến đi)
     * Hàm này trả về số tiền COD thu được để UseCase biết đường cộng/trừ vào ví tài xế ở tầng Application
     */
    public void deliverSuccess() {
        if (this.rootEntity.getStatus() != DeliveryStatus.PICKED_UP) {
            throw new IllegalStateException("Đơn hàng chưa được lấy, không thể xác nhận giao thành công!");
        }
        this.rootEntity.setStatus(DeliveryStatus.DELIVERED);
        this.rootEntity.setDropoffTime(ZonedDateTime.now());
    }

    /**
     * 8. KHÁCH HÀNG HỦY ĐƠN (Chỉ được hủy khi xế chưa lấy hàng khỏi quán)
     */
    public void cancelByCustomer(String reason) {
        DeliveryStatus currentStatus = this.rootEntity.getStatus();
        if (currentStatus == DeliveryStatus.PICKED_UP || currentStatus == DeliveryStatus.DELIVERED) {
            throw new IllegalStateException("Nhà hàng đã chuẩn bị xong hoặc tài xế đang giao, không thể hủy!");
        }
        this.rootEntity.setStatus(DeliveryStatus.FAILED);
        this.rootEntity.setReasonFailed("CUSTOMER_CANCELLED: " + reason);
    }

    /**
     * 9. TÀI XẾ BÁO SỰ CỐ / HỦY CHUYẾN GIỮA ĐƯỜNG (Xe hỏng, tai nạn, khách boom hàng...)
     */
    public void cancelByDriver(String reason) {
        DeliveryStatus currentStatus = this.rootEntity.getStatus();
        if (currentStatus != DeliveryStatus.ACCEPTED &&
                currentStatus != DeliveryStatus.ARRIVED_AT_RESTAURANT &&
                currentStatus != DeliveryStatus.PICKED_UP) {
            throw new IllegalStateException("Trạng thái hiện tại không hợp lệ để tài xế báo hủy chuyến!");
        }
        this.rootEntity.setStatus(DeliveryStatus.FAILED);
        this.rootEntity.setReasonFailed("DRIVER_FAILED: " + reason);
    }
}
