package mss301.se1911.group.assignment.restaurantservice.application.command;

import java.time.LocalTime;
import java.util.UUID;

public record UpdateRestaurantCommand(
        UUID restaurantId,
        String name,
        String address,
        String imageUrl,
        String description,
        String cuisineType,
        LocalTime openingTime,
        LocalTime closingTime
) {}
