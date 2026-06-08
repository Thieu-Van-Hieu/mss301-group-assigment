package mss301.se1911.group.assignment.deliveryservice.domain.repository;

import mss301.se1911.group.assignment.deliveryservice.domain.aggregate.DriverProfileAggregate;
import mss301.se1911.group.assignment.deliveryservice.domain.entity.DriverProfileEntity;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.common.PageResult;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.criteria.DriverQueryCriteria;

import java.util.Optional;
import java.util.UUID;

public interface DriverProfileRepository {
    Optional<DriverProfileAggregate> findById(UUID driverId);
    void save(DriverProfileAggregate driverProfile);

    PageResult<DriverProfileAggregate> findAllWithFilter(DriverQueryCriteria criteria, int page, int size);
}
