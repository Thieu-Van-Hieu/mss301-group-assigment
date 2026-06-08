package mss301.se1911.group.assignment.deliveryservice.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.deliveryservice.domain.aggregate.DriverProfileAggregate;
import mss301.se1911.group.assignment.deliveryservice.domain.entity.DriverProfileEntity;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.DriverProfileRepository;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.common.PageResult;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.criteria.DriverQueryCriteria;
import mss301.se1911.group.assignment.deliveryservice.infrastructure.persistence.JpaDriverProfileRepository;
import mss301.se1911.group.assignment.deliveryservice.infrastructure.specification.DriverProfileSpecification;
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
public class DriverProfileRepositoryImpl implements DriverProfileRepository {

    private final JpaDriverProfileRepository jpaRepository;

    @Override
    public Optional<DriverProfileAggregate> findById(UUID id) {
        return jpaRepository.findById(id).map(DriverProfileAggregate::new);
    }

    @Override
    public void save(DriverProfileAggregate driverProfile) {
        jpaRepository.save(driverProfile.getRootEntity());
    }

    @Override
    public PageResult<DriverProfileAggregate> findAllWithFilter(DriverQueryCriteria criteria, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Specification<DriverProfileEntity> spec = DriverProfileSpecification.getSpecification(criteria);

        Page<DriverProfileEntity> jpaPage = jpaRepository.findAll(spec, pageable);

        List<DriverProfileAggregate> content = jpaPage.getContent().stream()
                .map(DriverProfileAggregate::new)
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