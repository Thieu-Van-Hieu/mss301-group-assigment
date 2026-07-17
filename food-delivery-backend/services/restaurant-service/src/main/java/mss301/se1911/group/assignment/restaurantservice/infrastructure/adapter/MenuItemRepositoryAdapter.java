package mss301.se1911.group.assignment.restaurantservice.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.restaurantservice.domain.aggregate.MenuItemAggregate;
import mss301.se1911.group.assignment.restaurantservice.domain.entity.MenuItemEntity;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.MenuItemRepository;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.common.PageResult;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.criteria.MenuItemQueryCriteria;
import mss301.se1911.group.assignment.restaurantservice.infrastructure.persistence.JpaMenuItemRepository;
import mss301.se1911.group.assignment.restaurantservice.infrastructure.specification.MenuItemSpecification;
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
public class MenuItemRepositoryAdapter implements MenuItemRepository {

    private final JpaMenuItemRepository jpaMenuItemRepository;

    @Override
    public void save(MenuItemAggregate menuItemAggregate) {
        jpaMenuItemRepository.save(menuItemAggregate.getRootEntity());
    }

    @Override
    public Optional<MenuItemAggregate> findById(UUID id) {
        return jpaMenuItemRepository.findById(id).map(MenuItemAggregate::new);
    }

    @Override
    public void deleteById(UUID id) {
        jpaMenuItemRepository.deleteById(id);
    }

    @Override
    public PageResult<MenuItemAggregate> findAllWithFilter(MenuItemQueryCriteria criteria, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<MenuItemEntity> spec = MenuItemSpecification.getSpecification(criteria);

        Page<MenuItemEntity> jpaPage = jpaMenuItemRepository.findAll(spec, pageable);

        List<MenuItemAggregate> content = jpaPage.getContent().stream()
                .map(MenuItemAggregate::new)
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
