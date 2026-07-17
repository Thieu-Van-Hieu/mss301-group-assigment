package mss301.se1911.group.assignment.restaurantservice.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.restaurantservice.domain.aggregate.RestaurantAggregate;
import mss301.se1911.group.assignment.restaurantservice.domain.entity.RestaurantEntity;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.RestaurantRepository;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.common.PageResult;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.criteria.RestaurantQueryCriteria;
import mss301.se1911.group.assignment.restaurantservice.infrastructure.persistence.JpaRestaurantRepository;
import mss301.se1911.group.assignment.restaurantservice.infrastructure.specification.RestaurantSpecification;
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
public class RestaurantRepositoryAdapter implements RestaurantRepository {

    private final JpaRestaurantRepository jpaRestaurantRepository;

    @Override
    public void save(RestaurantAggregate restaurantAggregate) {
        jpaRestaurantRepository.save(restaurantAggregate.getRootEntity());
    }

    @Override
    public Optional<RestaurantAggregate> findById(UUID id) {
        return jpaRestaurantRepository.findById(id).map(RestaurantAggregate::new);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRestaurantRepository.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRestaurantRepository.deleteById(id);
    }

    @Override
    public PageResult<RestaurantAggregate> findAllWithFilter(RestaurantQueryCriteria criteria, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<RestaurantEntity> spec = RestaurantSpecification.getSpecification(criteria);

        Page<RestaurantEntity> jpaPage = jpaRestaurantRepository.findAll(spec, pageable);

        List<RestaurantAggregate> content = jpaPage.getContent().stream()
                .map(RestaurantAggregate::new)
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
