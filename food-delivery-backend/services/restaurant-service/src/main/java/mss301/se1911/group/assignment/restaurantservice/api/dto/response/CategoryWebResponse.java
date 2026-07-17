package mss301.se1911.group.assignment.restaurantservice.api.dto.response;

import mss301.se1911.group.assignment.restaurantservice.application.dto.CategoryResponse;

import java.util.UUID;

public record CategoryWebResponse(
        UUID id,
        UUID restaurantId,
        String name
) {
    public static CategoryWebResponse fromAppDto(CategoryResponse dto) {
        if (dto == null) return null;
        return new CategoryWebResponse(
                dto.id(),
                dto.restaurantId(),
                dto.name()
        );
    }
}
