package mss301.se1911.group.assignment.restaurantservice.infrastructure.persistence;

import mss301.se1911.group.assignment.restaurantservice.domain.entity.MenuItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface JpaMenuItemRepository extends JpaRepository<MenuItemEntity, UUID>,
        JpaSpecificationExecutor<MenuItemEntity> {
}
