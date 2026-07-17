package mss301.se1911.group.assignment.restaurantservice.infrastructure.persistence;

import mss301.se1911.group.assignment.restaurantservice.domain.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaCategoryRepository extends JpaRepository<CategoryEntity, UUID> {

    List<CategoryEntity> findByRestaurantId(UUID restaurantId);
}
