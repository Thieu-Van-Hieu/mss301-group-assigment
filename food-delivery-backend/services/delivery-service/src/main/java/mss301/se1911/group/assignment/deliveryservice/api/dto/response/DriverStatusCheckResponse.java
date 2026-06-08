package mss301.se1911.group.assignment.deliveryservice.api.dto.response;

import mss301.se1911.group.assignment.deliveryservice.domain.enums.DriverStatus;

public record DriverStatusCheckResponse(
        String driverId,
        DriverStatus status,
        boolean requireOnboarding, // true: FE bắt buộc redirect sang trang nhập hồ sơ xe
        String message
) {}