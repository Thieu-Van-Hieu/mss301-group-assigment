package mss301.se1911.group.assignment.restaurantservice.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(
        @NotBlank(message = "Tên phân loại không được để trống")
        String name
) {}
