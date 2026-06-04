package mss301.se1911.group.assignment.deliveryservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import mss301.se1911.group.assignment.deliveryservice.domain.enums.DriverStatus;
import mss301.se1911.group.assignment.deliveryservice.domain.enums.VehicleType;

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

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "license_plate", nullable = false, unique = true)
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @Column(name = "is_online")
    private boolean online;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DriverStatus status;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "deleted_at")
    private ZonedDateTime deletedAt;
}
