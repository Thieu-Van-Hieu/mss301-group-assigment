package mss301.se1911.group.assignment.restaurantservice.application.usecase;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.restaurantservice.application.dto.CategoryResponse;
import mss301.se1911.group.assignment.restaurantservice.domain.entity.CategoryEntity;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.CategoryRepository;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryUseCases {

    private final CategoryRepository categoryRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public CategoryResponse create(UUID restaurantId, String name) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new IllegalArgumentException("Không tìm thấy nhà hàng với ID: " + restaurantId);
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tên phân loại không được để trống!");
        }

        CategoryEntity entity = CategoryEntity.builder()
                .id(UUID.randomUUID())
                .restaurantId(restaurantId)
                .name(name)
                .createdAt(ZonedDateTime.now())
                .build();

        return CategoryResponse.fromEntity(categoryRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> listByRestaurant(UUID restaurantId) {
        return categoryRepository.findByRestaurantId(restaurantId).stream()
                .map(CategoryResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void delete(UUID categoryId) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phân loại với ID: " + categoryId));
        categoryRepository.deleteById(categoryId);
    }
}
