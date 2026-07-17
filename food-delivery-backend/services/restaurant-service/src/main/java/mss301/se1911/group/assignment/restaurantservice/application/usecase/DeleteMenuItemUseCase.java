package mss301.se1911.group.assignment.restaurantservice.application.usecase;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.restaurantservice.domain.aggregate.MenuItemAggregate;
import mss301.se1911.group.assignment.restaurantservice.domain.event.RestaurantEventPublisher;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.MenuItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteMenuItemUseCase {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantEventPublisher eventPublisher;

    @Transactional
    public void execute(UUID menuItemId) {
        MenuItemAggregate aggregate = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy món ăn với ID: " + menuItemId));

        UUID restaurantId = aggregate.getRootEntity().getRestaurantId();

        menuItemRepository.deleteById(menuItemId);

        eventPublisher.publishMenuUpdated(restaurantId, menuItemId, "DELETED");
    }
}
