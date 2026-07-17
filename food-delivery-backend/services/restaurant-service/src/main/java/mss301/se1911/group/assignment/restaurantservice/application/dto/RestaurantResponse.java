package mss301.se1911.group.assignment.restaurantservice.application.dto;

import mss301.se1911.group.assignment.restaurantservice.domain.aggregate.RestaurantAggregate;
import mss301.se1911.group.assignment.restaurantservice.domain.entity.RestaurantEntity;

import java.time.LocalTime;
import java.util.UUID;

public record RestaurantResponse(
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
    public static RestaurantResponse fromAggregate(RestaurantAggregate aggregate) {
        if (aggregate == null) return null;
        RestaurantEntity entity = aggregate.getRootEntity();
        return new RestaurantResponse(
                entity.getId(),
                entity.getOwnerId(),
                entity.getName(),
                entity.getAddress(),
                entity.getImageUrl(),
                entity.getDescription(),
                entity.getCuisineType(),
                entity.getStatus().name(),
                aggregate.isOpenNow(),
                entity.getOpeningTime(),
                entity.getClosingTime()
        );
    }
}
