package mss301.se1911.group.assignment.deliveryservice.domain.aggregate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.deliveryservice.domain.entity.DeliveryEntity;
import mss301.se1911.group.assignment.deliveryservice.domain.entity.DriverProfileEntity;
import mss301.se1911.group.assignment.deliveryservice.domain.enums.DeliveryStatus;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class DeliveryAggregate {

    private final DeliveryEntity rootEntity;

    /**
     * Trạng thái 1: Hệ thống gán đơn cho 1 tài xế tiềm năng (Matching Engine gọi)
     */
    public void assignDriver(DriverProfileEntity driverEntity) {
        if (this.rootEntity.getStatus() != DeliveryStatus.READY_TO_MATCH ) {
            throw new IllegalStateException("Đơn hàng không ở trạng thái sẵn sàng để điều phối!");
        }

        this.rootEntity.setDriver(driverEntity);
        this.rootEntity.setStatus(DeliveryStatus.ASSIGNED);
    }

    /**
     * Trạng thái 2 (Áp dụng Luật SLA 30s): Tài xế từ chối hoặc hết 30s không phản hồi
     */
    public void timeoutOrRejectAssignment() {
        if (this.rootEntity.getStatus() != DeliveryStatus.ASSIGNED) {
            throw new IllegalStateException("Đơn hàng không ở trạng thái đang gán để có thể hủy bỏ!");
        }

        this.rootEntity.setDriver(null); // Gỡ tài xế ra khỏi đơn
        this.rootEntity.setStatus(DeliveryStatus.READY_TO_MATCH); // Trả về bể đơn để quét người khác
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
     * Trạng thái thất bại: HỦY CHUYẾN ĐI (Hệ thống hoặc nhà hàng hủy)
     */
    public void failDelivery(String reason) {
        // Không cho phép hủy khi đơn đã hoàn thành thành công
        if (this.rootEntity.getStatus() == DeliveryStatus.DELIVERED) {
            throw new IllegalStateException("Đơn hàng đã hoàn thành, không thể hủy!");
        }

        this.rootEntity.setStatus(DeliveryStatus.FAILED);
        this.rootEntity.setReasonFailed(reason);
    }
}
