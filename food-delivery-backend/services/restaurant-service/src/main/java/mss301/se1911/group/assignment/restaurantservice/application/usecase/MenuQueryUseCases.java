package mss301.se1911.group.assignment.restaurantservice.application.usecase;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.restaurantservice.application.dto.MenuItemResponse;
import mss301.se1911.group.assignment.restaurantservice.domain.aggregate.MenuItemAggregate;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.MenuItemRepository;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.common.PageResult;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.criteria.MenuItemQueryCriteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuQueryUseCases {

    private final MenuItemRepository menuItemRepository;

    @Transactional(readOnly = true)
    public MenuItemResponse getById(UUID menuItemId) {
        return menuItemRepository.findById(menuItemId)
                .map(MenuItemResponse::fromAggregate)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy món ăn với ID: " + menuItemId));
    }

    @Transactional(readOnly = true)
    public PageResult<MenuItemResponse> search(MenuItemQueryCriteria criteria, int page, int size) {
        PageResult<MenuItemAggregate> raw = menuItemRepository.findAllWithFilter(criteria, page, size);

        List<MenuItemResponse> content = raw.content().stream()
                .map(MenuItemResponse::fromAggregate)
                .toList();

        return new PageResult<>(
                content,
                raw.totalElements(),
                raw.totalPages(),
                raw.pageNumber(),
                raw.pageSize()
        );
    }
}
