package mss301.se1911.group.assignment.restaurantservice.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.restaurantservice.api.dto.request.ChangeStatusRequest;
import mss301.se1911.group.assignment.restaurantservice.api.dto.request.CreateRestaurantRequest;
import mss301.se1911.group.assignment.restaurantservice.api.dto.request.UpdateRestaurantRequest;
import mss301.se1911.group.assignment.restaurantservice.api.dto.response.RestaurantWebResponse;
import mss301.se1911.group.assignment.restaurantservice.application.dto.RestaurantResponse;
import mss301.se1911.group.assignment.restaurantservice.application.usecase.ChangeRestaurantStatusUseCase;
import mss301.se1911.group.assignment.restaurantservice.application.usecase.CreateRestaurantUseCase;
import mss301.se1911.group.assignment.restaurantservice.application.usecase.DeleteRestaurantUseCase;
import mss301.se1911.group.assignment.restaurantservice.application.usecase.RestaurantQueryUseCases;
import mss301.se1911.group.assignment.restaurantservice.application.usecase.UpdateRestaurantUseCase;
import mss301.se1911.group.assignment.restaurantservice.domain.enums.RestaurantStatus;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.common.PageResult;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.criteria.RestaurantQueryCriteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final CreateRestaurantUseCase createRestaurantUseCase;
    private final UpdateRestaurantUseCase updateRestaurantUseCase;
    private final DeleteRestaurantUseCase deleteRestaurantUseCase;
    private final ChangeRestaurantStatusUseCase changeRestaurantStatusUseCase;
    private final RestaurantQueryUseCases restaurantQueryUseCases;

    @PostMapping
    public ResponseEntity<RestaurantWebResponse> create(
            @RequestHeader(value = "X-User-Id", required = false) UUID ownerId,
            @Valid @RequestBody CreateRestaurantRequest request) {

        RestaurantResponse dto = createRestaurantUseCase.execute(request.toCommand(ownerId));
        return ResponseEntity.status(HttpStatus.CREATED).body(RestaurantWebResponse.fromAppDto(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantWebResponse> getById(@PathVariable UUID id) {
        RestaurantResponse dto = restaurantQueryUseCases.getById(id);
        return ResponseEntity.ok(RestaurantWebResponse.fromAppDto(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantWebResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRestaurantRequest request) {

        RestaurantResponse dto = updateRestaurantUseCase.execute(request.toCommand(id));
        return ResponseEntity.ok(RestaurantWebResponse.fromAppDto(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteRestaurantUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Tìm kiếm & lọc nhà hàng theo tên, loại ẩm thực, trạng thái (có phân trang).
     */
    @GetMapping
    public ResponseEntity<PageResult<RestaurantWebResponse>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String cuisineType,
            @RequestParam(required = false) RestaurantStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        RestaurantQueryCriteria criteria = new RestaurantQueryCriteria(name, cuisineType, status);
        PageResult<RestaurantResponse> result = restaurantQueryUseCases.search(criteria, page, size);

        List<RestaurantWebResponse> content = result.content().stream()
                .map(RestaurantWebResponse::fromAppDto)
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
     * Quản lý trạng thái: mở / đóng cửa theo thời gian thực.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<RestaurantWebResponse> changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeStatusRequest request) {

        RestaurantResponse dto = changeRestaurantStatusUseCase.execute(id, request.status());
        return ResponseEntity.ok(RestaurantWebResponse.fromAppDto(dto));
    }
}
