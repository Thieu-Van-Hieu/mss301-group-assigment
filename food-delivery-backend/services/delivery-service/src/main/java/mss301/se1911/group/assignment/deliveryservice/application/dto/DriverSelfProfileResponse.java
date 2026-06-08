package mss301.se1911.group.assignment.deliveryservice.application.dto;

import mss301.se1911.group.assignment.deliveryservice.domain.enums.DriverStatus;
import mss301.se1911.group.assignment.deliveryservice.domain.enums.VehicleType;

import java.math.BigDecimal;
import java.util.UUID;

public record DriverSelfProfileResponse(
        UUID driverId,
        String fullName,
        String phoneNumber,
        String email,
        String identityNumber,
        String licenseNumber,
        String licensePlate,
        VehicleType vehicleType,
        String vehicleColor,
        boolean online,
        BigDecimal walletBalance, // Thống kê tài chính: Số tiền đang có/nợ hệ thống
        DriverStatus status
) {
}
