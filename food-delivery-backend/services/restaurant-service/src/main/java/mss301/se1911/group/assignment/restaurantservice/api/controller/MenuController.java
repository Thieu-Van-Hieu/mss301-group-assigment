package mss301.se1911.group.assignment.restaurantservice.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.restaurantservice.api.dto.request.CreateMenuItemRequest;
import mss301.se1911.group.assignment.restaurantservice.api.dto.request.UpdateMenuItemRequest;
import mss301.se1911.group.assignment.restaurantservice.api.dto.response.MenuItemWebResponse;
import mss301.se1911.group.assignment.restaurantservice.application.dto.MenuItemResponse;
import mss301.se1911.group.assignment.restaurantservice.application.usecase.AddMenuItemUseCase;
import mss301.se1911.group.assignment.restaurantservice.application.usecase.DeleteMenuItemUseCase;
import mss301.se1911.group.assignment.restaurantservice.application.usecase.MenuQueryUseCases;
import mss301.se1911.group.assignment.restaurantservice.application.usecase.UpdateMenuItemUseCase;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.common.PageResult;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.criteria.MenuItemQueryCriteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MenuController {

    private final AddMenuItemUseCase addMenuItemUseCase;
    private final UpdateMenuItemUseCase updateMenuItemUseCase;
    private final DeleteMenuItemUseCase deleteMenuItemUseCase;
    private final MenuQueryUseCases menuQueryUseCases;

    /**
     * Thêm món ăn vào menu của một nhà hàng.
     */
    @PostMapping("/restaurants/{restaurantId}/menu")
    public ResponseEntity<MenuItemWebResponse> addMenuItem(
            @PathVariable UUID restaurantId,
            @Valid @RequestBody CreateMenuItemRequest request) {

        MenuItemResponse dto = addMenuItemUseCase.execute(request.toCommand(restaurantId));
        return ResponseEntity.status(HttpStatus.CREATED).body(MenuItemWebResponse.fromAppDto(dto));
    }

    /**
     * Xem & tìm kiếm menu của một nhà hàng: theo tên món, phân loại, khoảng giá.
     */
    @GetMapping("/restaurants/{restaurantId}/menu")
    public ResponseEntity<PageResult<MenuItemWebResponse>> getMenu(
            @PathVariable UUID restaurantId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        MenuItemQueryCriteria criteria = new MenuItemQueryCriteria(
                restaurantId, name, categoryId, minPrice, maxPrice, available);
        PageResult<MenuItemResponse> result = menuQueryUseCases.search(criteria, page, size);

        List<MenuItemWebResponse> content = result.content().stream()
                .map(MenuItemWebResponse::fromAppDto)
                .toList();

        return ResponseEntity.ok(new PageResult<>(
                content,
                result.totalElements(),
                result.totalPages(),
                result.pageNumber(),
                result.pageSize()
        ));
    }

    /**
     * Sửa thông tin một món ăn.
     */
    @PutMapping("/menu/{itemId}")
    public ResponseEntity<MenuItemWebResponse> updateMenuItem(
            @PathVariable UUID itemId,
            @Valid @RequestBody UpdateMenuItemRequest request) {

        MenuItemResponse dto = updateMenuItemUseCase.execute(request.toCommand(itemId));
        return ResponseEntity.ok(MenuItemWebResponse.fromAppDto(dto));
    }

    /**
     * Xóa một món ăn.
     */
    @DeleteMapping("/menu/{itemId}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable UUID itemId) {
        deleteMenuItemUseCase.execute(itemId);
        return ResponseEntity.noContent().build();
    }
}
