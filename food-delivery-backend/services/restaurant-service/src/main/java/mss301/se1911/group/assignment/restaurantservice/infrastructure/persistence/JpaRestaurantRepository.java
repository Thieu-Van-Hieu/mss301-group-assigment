package mss301.se1911.group.assignment.restaurantservice.infrastructure.persistence;

import mss301.se1911.group.assignment.restaurantservice.domain.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface JpaRestaurantRepository extends JpaRepository<RestaurantEntity, UUID>,
        JpaSpecificationExecutor<RestaurantEntity> {
}
