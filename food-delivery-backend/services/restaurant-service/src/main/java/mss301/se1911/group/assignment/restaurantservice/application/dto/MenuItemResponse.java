package mss301.se1911.group.assignment.restaurantservice.application.dto;

import mss301.se1911.group.assignment.restaurantservice.domain.aggregate.MenuItemAggregate;
import mss301.se1911.group.assignment.restaurantservice.domain.entity.MenuItemEntity;

import java.math.BigDecimal;
import java.util.UUID;

public record MenuItemResponse(
        UUID id,
        UUID restaurantId,
        UUID categoryId,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        boolean available
) {
    public static MenuItemResponse fromAggregate(MenuItemAggregate aggregate) {
        if (aggregate == null) return null;
        MenuItemEntity entity = aggregate.getRootEntity();
        return new MenuItemResponse(
                entity.getId(),
                entity.getRestaurantId(),
                entity.getCategoryId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getImageUrl(),
                entity.isAvailable()
        );
    }
}
