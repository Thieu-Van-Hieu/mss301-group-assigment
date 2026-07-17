package mss301.se1911.group.assignment.restaurantservice.domain.repository;

import mss301.se1911.group.assignment.restaurantservice.domain.aggregate.RestaurantAggregate;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.common.PageResult;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.criteria.RestaurantQueryCriteria;

import java.util.Optional;
import java.util.UUID;

public interface RestaurantRepository {

    void save(RestaurantAggregate restaurantAggregate);

    Optional<RestaurantAggregate> findById(UUID id);

    boolean existsById(UUID id);

    void deleteById(UUID id);

    PageResult<RestaurantAggregate> findAllWithFilter(RestaurantQueryCriteria criteria, int page, int size);
}
