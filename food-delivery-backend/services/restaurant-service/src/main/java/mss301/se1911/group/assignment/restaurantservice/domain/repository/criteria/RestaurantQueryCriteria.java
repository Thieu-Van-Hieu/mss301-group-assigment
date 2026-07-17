package mss301.se1911.group.assignment.restaurantservice.domain.repository.criteria;

import mss301.se1911.group.assignment.restaurantservice.domain.enums.RestaurantStatus;

/**
 * Điều kiện tìm kiếm & lọc nhà hàng: theo tên, loại ẩm thực, trạng thái mở/đóng cửa.
 */
public record RestaurantQueryCriteria(
        String name,
        String cuisineType,
        RestaurantStatus status
) {}
