package mss301.se1911.group.assignment.deliveryservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import mss301.se1911.group.assignment.deliveryservice.domain.enums.DriverStatus;
import mss301.se1911.group.assignment.deliveryservice.domain.enums.VehicleType;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "driver_profiles")
public class DriverProfileEntity {

    @Id
    @Column(name = "driver_id")
    private UUID driverId;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "identity_number", length = 20)
    private String identityNumber;

    @Column(name = "license_number", length = 20)
    private String licenseNumber;

    @Column(name = "license_plate", length = 20)
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", length = 20)
    private VehicleType vehicleType;

    @Column(name = "vehicle_color", length = 30)
    private String vehicleColor;

    @Column(name = "is_online")
    private boolean online;

    @Column(
            name = "wallet_balance",
            precision = 15,
            scale = 2
    )
    private BigDecimal walletBalance;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private DriverStatus status;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "deleted_at")
    private ZonedDateTime deletedAt;
}