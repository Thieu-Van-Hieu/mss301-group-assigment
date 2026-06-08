package mss301.se1911.group.assignment.deliveryservice.domain.repository.criteria;

import mss301.se1911.group.assignment.deliveryservice.domain.enums.DriverStatus;

/**
 * Object chứa các điều kiện lọc tài xế do Admin gửi lên
 */
public record DriverQueryCriteria(
        DriverStatus status,
        Boolean online,
        String searchKeyword // Dùng để search gộp theo Tên hoặc Số điện thoại
) {}
