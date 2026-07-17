package mss301.se1911.group.assignment.restaurantservice.domain.repository.criteria;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Điều kiện tìm kiếm & lọc món ăn: theo tên, phân loại (category) và khoảng giá.
 */
public record MenuItemQueryCriteria(
        UUID restaurantId,
        String name,
        UUID categoryId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Boolean available
) {}
