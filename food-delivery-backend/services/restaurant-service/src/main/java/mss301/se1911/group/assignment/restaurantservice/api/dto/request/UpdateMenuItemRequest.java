package mss301.se1911.group.assignment.restaurantservice.api.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
import mss301.se1911.group.assignment.restaurantservice.application.command.UpdateMenuItemCommand;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateMenuItemRequest(
        String name,
        String description,

        @PositiveOrZero(message = "Giá món ăn phải lớn hơn hoặc bằng 0")
        BigDecimal price,

        String imageUrl,
        UUID categoryId,
        Boolean available
) {
    public UpdateMenuItemCommand toCommand(UUID menuItemId) {
        return new UpdateMenuItemCommand(
                menuItemId,
                this.categoryId,
                this.name,
                this.description,
                this.price,
                this.imageUrl,
                this.available
        );
    }
}
