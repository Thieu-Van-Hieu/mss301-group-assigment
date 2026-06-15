package mss301.se1911.group.assignment.deliveryservice.application.usecase;

import mss301.se1911.group.assignment.deliveryservice.application.dto.DeliveryResponse;
import mss301.se1911.group.assignment.deliveryservice.domain.aggregate.DeliveryAggregate;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.DeliveryRepository;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.common.PageResult;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.criteria.DeliveryQueryCriteria;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryQueryUseCases {

    private final DeliveryRepository deliveryRepository;

    // 1. GetActiveDeliveryUseCase: Lấy đơn đang chạy của xế
    public DeliveryResponse getActiveDelivery(UUID driverId) {
        return deliveryRepository.findActiveDeliveryByDriverId(driverId)
                .map(agg -> DeliveryResponse.fromEntity(agg.getRootEntity()))
                .orElse(null); // Trả về null nếu xế đang rảnh (không có đơn)
    }

    // 2. GetDriverDeliveryHistoryUseCase: Lịch sử phân trang cho xế (Infinite Scroll)
    public PageResult<DeliveryResponse> getDriverDeliveryHistory(DeliveryQueryCriteria criteria, int page, int size) {
        PageResult<DeliveryAggregate> result = deliveryRepository.findAllWithFilter(criteria, page, size);

        java.util.List<DeliveryResponse> dtos = result.content().stream()
                .map(agg -> DeliveryResponse.fromEntity(agg.getRootEntity()))
                .toList();

        return new PageResult<>(dtos, result.totalElements(), result.totalPages(), result.pageNumber(), result.pageSize());
    }

    // 3. GetCustomerDeliveryTrackingUseCase: Lấy thông tin tracking cho khách
    // Lưu ý: Cần bổ sung thêm method findByOrderId vào Interface DeliveryRepository
    public DeliveryResponse getCustomerTrackingInfo(UUID orderId) {
        // Tạm gọi theo ID của delivery. Bạn cần custom lại repo để findByOrderId nhé.
        return null;
    }
}