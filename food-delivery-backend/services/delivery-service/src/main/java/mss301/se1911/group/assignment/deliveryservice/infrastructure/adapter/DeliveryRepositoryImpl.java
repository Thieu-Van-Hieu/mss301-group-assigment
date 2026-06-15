package mss301.se1911.group.assignment.deliveryservice.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.deliveryservice.domain.aggregate.DeliveryAggregate;
import mss301.se1911.group.assignment.deliveryservice.domain.aggregate.DriverProfileAggregate;
import mss301.se1911.group.assignment.deliveryservice.domain.entity.DeliveryEntity;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.DeliveryRepository;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.common.PageResult;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.criteria.DeliveryQueryCriteria;
import mss301.se1911.group.assignment.deliveryservice.infrastructure.persistence.JpaDeliveryRepository;
import mss301.se1911.group.assignment.deliveryservice.infrastructure.specification.DeliverySpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeliveryRepositoryImpl implements DeliveryRepository {

    private final JpaDeliveryRepository jpaDeliveryRepository;

    @Override
    public void save(DeliveryAggregate deliveryAggregate) {
        // Lưu Entity từ Aggregate xuống DB
        jpaDeliveryRepository.save(deliveryAggregate.getRootEntity());
    }

    @Override
    public Optional<DeliveryAggregate> findById(UUID id) {
        return jpaDeliveryRepository.findById(id).map(DeliveryAggregate::new);
    }

    @Override
    public Optional<DeliveryAggregate> findActiveDeliveryByDriverId(UUID driverId) {
        return jpaDeliveryRepository.findActiveDeliveryByDriverId(driverId)
                .map(DeliveryAggregate::new);
    }

    @Override
    public PageResult<DeliveryAggregate> findAllWithFilter(DeliveryQueryCriteria criteria, int page, int size) {
        // 1. Tạo Pageable của Spring
        Pageable pageable = PageRequest.of(page, size);

        // 2. Lấy Specification từ criteria
        Specification<DeliveryEntity> spec = DeliverySpecification.getSpecification(criteria);

        // 3. Thực thi truy vấn
        Page<DeliveryEntity> jpaPage = jpaDeliveryRepository.findAll(spec, pageable);

        // 4. Map kết quả sang Aggregate và đóng gói về PageResult thuần Java
        List<DeliveryAggregate> content = jpaPage.getContent().stream()
                .map(DeliveryAggregate::new)
                .toList();

        return new PageResult<>(
                content,
                jpaPage.getTotalElements(),
                jpaPage.getTotalPages(),
                jpaPage.getNumber(),
                jpaPage.getSize()
        );
    }
}
