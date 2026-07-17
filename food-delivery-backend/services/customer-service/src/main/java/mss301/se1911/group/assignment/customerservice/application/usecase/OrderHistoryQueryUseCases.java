package mss301.se1911.group.assignment.customerservice.application.usecase;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.customerservice.application.dto.OrderHistoryResponse;
import mss301.se1911.group.assignment.customerservice.domain.entity.OrderHistoryEntity;
import mss301.se1911.group.assignment.customerservice.domain.repository.OrderHistoryRepository;
import mss301.se1911.group.assignment.customerservice.domain.repository.common.PageResult;
import mss301.se1911.group.assignment.customerservice.domain.repository.criteria.OrderHistoryQueryCriteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderHistoryQueryUseCases {

    private final OrderHistoryRepository orderHistoryRepository;

    @Transactional(readOnly = true)
    public PageResult<OrderHistoryResponse> listByCustomer(UUID customerId, String status, int page, int size) {
        OrderHistoryQueryCriteria criteria = new OrderHistoryQueryCriteria(customerId, status);
        PageResult<OrderHistoryEntity> raw = orderHistoryRepository.findAllWithFilter(criteria, page, size);

        List<OrderHistoryResponse> content = raw.content().stream()
                .map(OrderHistoryResponse::fromEntity)
                .toList();

        return new PageResult<>(
                content,
                raw.totalElements(),
                raw.totalPages(),
                raw.pageNumber(),
                raw.pageSize()
        );
    }

    @Transactional(readOnly = true)
    public OrderHistoryResponse getDetail(UUID customerId, UUID orderId) {
        return orderHistoryRepository.findByOrderIdAndCustomerId(orderId, customerId)
                .map(OrderHistoryResponse::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Không tìm thấy đơn hàng " + orderId + " của khách hàng " + customerId));
    }
}
