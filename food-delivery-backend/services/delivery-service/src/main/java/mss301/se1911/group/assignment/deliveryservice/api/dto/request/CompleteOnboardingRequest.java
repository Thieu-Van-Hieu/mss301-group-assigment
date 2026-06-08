package mss301.se1911.group.assignment.deliveryservice.api.dto.request;

import mss301.se1911.group.assignment.deliveryservice.domain.enums.VehicleType;

/**
 * DTO nhận dữ liệu điền hồ sơ xe từ Frontend
 */
public record CompleteOnboardingRequest(
        String identityNumber,

        String licenseNumber,

        String licensePlate,

        VehicleType vehicleType,

        String vehicleColor
) {}