package mss301.se1911.group.assignment.restaurantservice.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateMenuItemCommand(
        UUID menuItemId,
        UUID categoryId,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        Boolean available
) {}
