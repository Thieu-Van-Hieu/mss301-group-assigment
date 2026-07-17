package mss301.se1911.group.assignment.restaurantservice.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.restaurantservice.api.dto.request.CreateCategoryRequest;
import mss301.se1911.group.assignment.restaurantservice.api.dto.response.CategoryWebResponse;
import mss301.se1911.group.assignment.restaurantservice.application.usecase.CategoryUseCases;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryUseCases categoryUseCases;

    /**
     * Tạo phân loại món ăn (category) cho một nhà hàng.
     */
    @PostMapping("/restaurants/{restaurantId}/categories")
    public ResponseEntity<CategoryWebResponse> create(
            @PathVariable UUID restaurantId,
            @Valid @RequestBody CreateCategoryRequest request) {

        CategoryWebResponse response = CategoryWebResponse.fromAppDto(
                categoryUseCases.create(restaurantId, request.name()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lấy danh sách phân loại của một nhà hàng.
     */
    @GetMapping("/restaurants/{restaurantId}/categories")
    public ResponseEntity<List<CategoryWebResponse>> list(@PathVariable UUID restaurantId) {
        List<CategoryWebResponse> responses = categoryUseCases.listByRestaurant(restaurantId).stream()
                .map(CategoryWebResponse::fromAppDto)
                .toList();
        return ResponseEntity.ok(responses);
    }

    /**
     * Xóa một phân loại.
     */
    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<Void> delete(@PathVariable UUID categoryId) {
        categoryUseCases.delete(categoryId);
        return ResponseEntity.noContent().build();
    }
}
