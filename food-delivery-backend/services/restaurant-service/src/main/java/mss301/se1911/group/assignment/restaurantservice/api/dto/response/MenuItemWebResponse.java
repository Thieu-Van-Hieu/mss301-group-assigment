package mss301.se1911.group.assignment.restaurantservice.api.dto.response;

import mss301.se1911.group.assignment.restaurantservice.application.dto.MenuItemResponse;

import java.math.BigDecimal;
import java.util.UUID;

public record MenuItemWebResponse(
        UUID id,
        UUID restaurantId,
        UUID categoryId,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        boolean available
) {
    public static MenuItemWebResponse fromAppDto(MenuItemResponse dto) {
        if (dto == null) return null;
        return new MenuItemWebResponse(
                dto.id(),
                dto.restaurantId(),
                dto.categoryId(),
                dto.name(),
                dto.description(),
                dto.price(),
                dto.imageUrl(),
                dto.available()
        );
    }
}
