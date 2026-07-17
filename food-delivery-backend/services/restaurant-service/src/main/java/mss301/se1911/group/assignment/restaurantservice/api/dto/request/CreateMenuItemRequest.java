package mss301.se1911.group.assignment.restaurantservice.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import mss301.se1911.group.assignment.restaurantservice.application.command.CreateMenuItemCommand;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateMenuItemRequest(
        @NotBlank(message = "Tên món ăn không được để trống")
        String name,

        String description,

        @NotNull(message = "Giá món ăn không được để trống")
        @PositiveOrZero(message = "Giá món ăn phải lớn hơn hoặc bằng 0")
        BigDecimal price,

        String imageUrl,
        UUID categoryId,
        Boolean available
) {
    public CreateMenuItemCommand toCommand(UUID restaurantId) {
        return new CreateMenuItemCommand(
                restaurantId,
                this.categoryId,
                this.name,
                this.description,
                this.price,
                this.imageUrl,
                this.available == null || this.available
        );
    }
}
