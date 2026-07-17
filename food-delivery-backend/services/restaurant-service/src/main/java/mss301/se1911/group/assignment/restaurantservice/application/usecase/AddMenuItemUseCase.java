package mss301.se1911.group.assignment.restaurantservice.application.usecase;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.restaurantservice.application.command.CreateMenuItemCommand;
import mss301.se1911.group.assignment.restaurantservice.application.dto.MenuItemResponse;
import mss301.se1911.group.assignment.restaurantservice.domain.aggregate.MenuItemAggregate;
import mss301.se1911.group.assignment.restaurantservice.domain.event.RestaurantEventPublisher;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.MenuItemRepository;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddMenuItemUseCase {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantEventPublisher eventPublisher;

    @Transactional
    public MenuItemResponse execute(CreateMenuItemCommand command) {
        if (!restaurantRepository.existsById(command.restaurantId())) {
            throw new IllegalArgumentException("Không tìm thấy nhà hàng với ID: " + command.restaurantId());
        }

        MenuItemAggregate aggregate = MenuItemAggregate.createNewMenuItem(
                command.restaurantId(),
                command.categoryId(),
                command.name(),
                command.description(),
                command.price(),
                command.imageUrl(),
                command.available()
        );

        menuItemRepository.save(aggregate);

        eventPublisher.publishMenuUpdated(command.restaurantId(), aggregate.getRootEntity().getId(), "CREATED");

        return MenuItemResponse.fromAggregate(aggregate);
    }
}
