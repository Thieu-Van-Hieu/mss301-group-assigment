package mss301.se1911.group.assignment.restaurantservice.application.dto;

import mss301.se1911.group.assignment.restaurantservice.domain.entity.CategoryEntity;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        UUID restaurantId,
        String name
) {
    public static CategoryResponse fromEntity(CategoryEntity entity) {
        if (entity == null) return null;
        return new CategoryResponse(
                entity.getId(),
                entity.getRestaurantId(),
                entity.getName()
        );
    }
}
