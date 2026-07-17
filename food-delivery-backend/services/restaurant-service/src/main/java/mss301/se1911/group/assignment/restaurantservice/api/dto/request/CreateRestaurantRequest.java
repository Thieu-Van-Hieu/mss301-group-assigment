package mss301.se1911.group.assignment.restaurantservice.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import mss301.se1911.group.assignment.restaurantservice.application.command.CreateRestaurantCommand;

import java.time.LocalTime;
import java.util.UUID;

public record CreateRestaurantRequest(
        @NotBlank(message = "Tên nhà hàng không được để trống")
        String name,

        @NotBlank(message = "Địa chỉ không được để trống")
        String address,

        String imageUrl,
        String description,
        String cuisineType,
        LocalTime openingTime,
        LocalTime closingTime
) {
    public CreateRestaurantCommand toCommand(UUID ownerId) {
        return new CreateRestaurantCommand(
                ownerId,
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
