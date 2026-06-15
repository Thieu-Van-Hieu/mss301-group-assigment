package mss301.se1911.group.assignment.deliveryservice.domain.repository;

import mss301.se1911.group.assignment.deliveryservice.domain.aggregate.DeliveryAggregate;
import mss301.se1911.group.assignment.deliveryservice.domain.aggregate.DriverProfileAggregate;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.common.PageResult;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.criteria.DeliveryQueryCriteria;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository {

    void save(DeliveryAggregate deliveryAggregate);

    Optional<DeliveryAggregate> findById(UUID id);

    // Dùng cho UseCase tìm chuyến đi HIỆN TẠI đang kích hoạt của tài xế
    Optional<DeliveryAggregate> findActiveDeliveryByDriverId(UUID driverId);

    // Dùng cho luồng Lịch sử (cuộn xuống load thêm)
    PageResult<DeliveryAggregate> findAllWithFilter(DeliveryQueryCriteria criteria, int page, int size);
}
