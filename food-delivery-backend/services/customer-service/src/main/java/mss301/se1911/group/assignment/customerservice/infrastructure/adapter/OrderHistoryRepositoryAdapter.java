package mss301.se1911.group.assignment.customerservice.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.customerservice.domain.entity.OrderHistoryEntity;
import mss301.se1911.group.assignment.customerservice.domain.repository.OrderHistoryRepository;
import mss301.se1911.group.assignment.customerservice.domain.repository.common.PageResult;
import mss301.se1911.group.assignment.customerservice.domain.repository.criteria.OrderHistoryQueryCriteria;
import mss301.se1911.group.assignment.customerservice.infrastructure.persistence.JpaOrderHistoryRepository;
import mss301.se1911.group.assignment.customerservice.infrastructure.specification.OrderHistorySpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderHistoryRepositoryAdapter implements OrderHistoryRepository {

    private final JpaOrderHistoryRepository jpaOrderHistoryRepository;

    @Override
    public void save(OrderHistoryEntity orderHistory) {
        jpaOrderHistoryRepository.save(orderHistory);
    }

    @Override
    public Optional<OrderHistoryEntity> findById(UUID orderId) {
        return jpaOrderHistoryRepository.findById(orderId);
    }

    @Override
    public Optional<OrderHistoryEntity> findByOrderIdAndCustomerId(UUID orderId, UUID customerId) {
        return jpaOrderHistoryRepository.findByOrderIdAndCustomerId(orderId, customerId);
    }

    @Override
    public PageResult<OrderHistoryEntity> findAllWithFilter(OrderHistoryQueryCriteria criteria, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<OrderHistoryEntity> spec = OrderHistorySpecification.getSpecification(criteria);
        Page<OrderHistoryEntity> jpaPage = jpaOrderHistoryRepository.findAll(spec, pageable);

        return new PageResult<>(
                jpaPage.getContent(),
                jpaPage.getTotalElements(),
                jpaPage.getTotalPages(),
                jpaPage.getNumber(),
                jpaPage.getSize()
        );
    }
}
