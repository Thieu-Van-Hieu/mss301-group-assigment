package mss301.se1911.group.assignment.customerservice.domain.repository;

import mss301.se1911.group.assignment.customerservice.domain.entity.OrderHistoryEntity;
import mss301.se1911.group.assignment.customerservice.domain.repository.common.PageResult;
import mss301.se1911.group.assignment.customerservice.domain.repository.criteria.OrderHistoryQueryCriteria;

import java.util.Optional;
import java.util.UUID;

public interface OrderHistoryRepository {

    void save(OrderHistoryEntity orderHistory);

    Optional<OrderHistoryEntity> findById(UUID orderId);

    Optional<OrderHistoryEntity> findByOrderIdAndCustomerId(UUID orderId, UUID customerId);

    PageResult<OrderHistoryEntity> findAllWithFilter(OrderHistoryQueryCriteria criteria, int page, int size);
}
