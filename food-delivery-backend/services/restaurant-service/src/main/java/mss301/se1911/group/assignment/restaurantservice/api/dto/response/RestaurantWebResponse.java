package mss301.se1911.group.assignment.restaurantservice.api.dto.response;

import mss301.se1911.group.assignment.restaurantservice.application.dto.RestaurantResponse;

import java.time.LocalTime;
import java.util.UUID;

public record RestaurantWebResponse(
        UUID id,
        UUID ownerId,
        String name,
        String address,
        String imageUrl,
        String description,
        String cuisineType,
        String status,
        boolean openNow,
        LocalTime openingTime,
        LocalTime closingTime
) {
    public static RestaurantWebResponse fromAppDto(RestaurantResponse dto) {
        if (dto == null) return null;
        return new RestaurantWebResponse(
                dto.id(),
                dto.ownerId(),
                dto.name(),
                dto.address(),
                dto.imageUrl(),
                dto.description(),
                dto.cuisineType(),
                dto.status(),
                dto.openNow(),
                dto.openingTime(),
                dto.closingTime()
        );
    }
}
