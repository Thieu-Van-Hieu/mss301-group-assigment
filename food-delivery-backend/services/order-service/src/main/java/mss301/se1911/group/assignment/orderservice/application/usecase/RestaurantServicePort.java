package mss301.se1911.group.assignment.orderservice.application.usecase;

import mss301.se1911.group.assignment.orderservice.application.command.OrderItemDto;

import java.util.List;
import java.util.UUID;

public interface RestaurantServicePort {
    void validateRestaurantAndItems(UUID restaurantId, List<OrderItemDto> items);
}
