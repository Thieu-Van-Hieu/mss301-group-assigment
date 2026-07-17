package mss301.se1911.group.assignment.restaurantservice.api.dto.request;

import jakarta.validation.constraints.NotNull;
import mss301.se1911.group.assignment.restaurantservice.domain.enums.RestaurantStatus;

public record ChangeStatusRequest(
        @NotNull(message = "Trạng thái không được để trống")
        RestaurantStatus status
) {}
