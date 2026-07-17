package mss301.se1911.group.assignment.restaurantservice.domain.repository;

import mss301.se1911.group.assignment.restaurantservice.domain.aggregate.MenuItemAggregate;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.common.PageResult;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.criteria.MenuItemQueryCriteria;

import java.util.Optional;
import java.util.UUID;

public interface MenuItemRepository {

    void save(MenuItemAggregate menuItemAggregate);

    Optional<MenuItemAggregate> findById(UUID id);

    void deleteById(UUID id);

    PageResult<MenuItemAggregate> findAllWithFilter(MenuItemQueryCriteria criteria, int page, int size);
}
