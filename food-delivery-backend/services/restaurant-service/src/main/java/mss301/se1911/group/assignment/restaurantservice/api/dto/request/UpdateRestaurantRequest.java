package mss301.se1911.group.assignment.restaurantservice.api.dto.request;

import mss301.se1911.group.assignment.restaurantservice.application.command.UpdateRestaurantCommand;

import java.time.LocalTime;
import java.util.UUID;

public record UpdateRestaurantRequest(
        String name,
        String address,
        String imageUrl,
        String description,
        String cuisineType,
        LocalTime openingTime,
        LocalTime closingTime
) {
    public UpdateRestaurantCommand toCommand(UUID restaurantId) {
        return new UpdateRestaurantCommand(
                restaurantId,
                this.name,
                this.address,
                this.imageUrl,
                this.description,
                this.cuisineType,
                this.openingTime,
                this.closingTime
        );
    }
}
