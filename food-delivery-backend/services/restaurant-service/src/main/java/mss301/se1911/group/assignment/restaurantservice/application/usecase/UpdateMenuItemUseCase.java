package mss301.se1911.group.assignment.restaurantservice.application.usecase;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.restaurantservice.application.command.UpdateMenuItemCommand;
import mss301.se1911.group.assignment.restaurantservice.application.dto.MenuItemResponse;
import mss301.se1911.group.assignment.restaurantservice.domain.aggregate.MenuItemAggregate;
import mss301.se1911.group.assignment.restaurantservice.domain.event.RestaurantEventPublisher;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.MenuItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateMenuItemUseCase {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantEventPublisher eventPublisher;

    @Transactional
    public MenuItemResponse execute(UpdateMenuItemCommand command) {
        MenuItemAggregate aggregate = menuItemRepository.findById(command.menuItemId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy món ăn với ID: " + command.menuItemId()));

        aggregate.update(
                command.categoryId(),
                command.name(),
                command.description(),
                command.price(),
                command.imageUrl(),
                command.available()
        );

        menuItemRepository.save(aggregate);

        eventPublisher.publishMenuUpdated(
                aggregate.getRootEntity().getRestaurantId(),
                aggregate.getRootEntity().getId(),
                "UPDATED"
        );

        return MenuItemResponse.fromAggregate(aggregate);
    }
}
