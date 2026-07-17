package mss301.se1911.group.assignment.restaurantservice.domain.repository;

import mss301.se1911.group.assignment.restaurantservice.domain.entity.CategoryEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {

    CategoryEntity save(CategoryEntity category);

    Optional<CategoryEntity> findById(UUID id);

    List<CategoryEntity> findByRestaurantId(UUID restaurantId);

    void deleteById(UUID id);
}
