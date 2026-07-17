package mss301.se1911.group.assignment.restaurantservice.domain.aggregate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.restaurantservice.domain.entity.MenuItemEntity;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class MenuItemAggregate {

    private final MenuItemEntity rootEntity;

    /**
     * CREATE: Factory Method thêm món ăn mới vào menu của một nhà hàng.
     */
    public static MenuItemAggregate createNewMenuItem(
            UUID restaurantId, UUID categoryId, String name, String description,
            BigDecimal price, String imageUrl, boolean available) {

        if (restaurantId == null) {
            throw new IllegalArgumentException("Món ăn phải thuộc về một nhà hàng!");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tên món ăn không được để trống!");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá món ăn không hợp lệ!");
        }

        MenuItemEntity entity = MenuItemEntity.builder()
                .id(UUID.randomUUID())
                .restaurantId(restaurantId)
                .categoryId(categoryId)
                .name(name)
                .description(description)
                .price(price)
                .imageUrl(imageUrl)
                .available(available)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();

        return new MenuItemAggregate(entity);
    }

    /**
     * UPDATE: Sửa thông tin món ăn (giá, mô tả, hình ảnh, phân loại, tình trạng còn hàng).
     */
    public void update(UUID categoryId, String name, String description,
                       BigDecimal price, String imageUrl, Boolean available) {
        if (name != null && !name.isBlank()) {
            this.rootEntity.setName(name);
        }
        if (price != null) {
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Giá món ăn không hợp lệ!");
            }
            this.rootEntity.setPrice(price);
        }
        if (available != null) {
            this.rootEntity.setAvailable(available);
        }
        this.rootEntity.setCategoryId(categoryId);
        this.rootEntity.setDescription(description);
        this.rootEntity.setImageUrl(imageUrl);
        this.rootEntity.setUpdatedAt(ZonedDateTime.now());
    }
}
