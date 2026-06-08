package mss301.se1911.group.assignment.deliveryservice.application.command;

import mss301.se1911.group.assignment.deliveryservice.domain.enums.VehicleType;

import java.util.UUID;

public record CompleteDriverOnboardingCommand(
        UUID driverId,
        String identityNumber,
        String licenseNumber,
        String licensePlate,
        VehicleType vehicleType,
        String vehicleColor
) {
}
