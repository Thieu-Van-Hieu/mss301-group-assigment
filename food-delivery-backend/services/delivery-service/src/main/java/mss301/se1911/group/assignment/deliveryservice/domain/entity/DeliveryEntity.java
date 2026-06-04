package mss301.se1911.group.assignment.deliveryservice.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mss301.se1911.group.assignment.deliveryservice.domain.enums.DeliveryStatus;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "deliveries")
public class DeliveryEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "order_id", nullable = false, unique = true)
    private UUID orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "driver_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_deliveries_driver")
    )
    private DriverProfileEntity driver;

    @Column(name = "status", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    @Column(name = "pickup_time")
    private ZonedDateTime pickupTime;

    @Column(name = "dropoff_time")
    private ZonedDateTime dropoffTime;

    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;
}
