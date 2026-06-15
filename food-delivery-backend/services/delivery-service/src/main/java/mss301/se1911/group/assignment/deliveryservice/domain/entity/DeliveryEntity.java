package mss301.se1911.group.assignment.deliveryservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import mss301.se1911.group.assignment.deliveryservice.domain.enums.DeliveryStatus;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "deliveries")
public class DeliveryEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "driver_id",
            foreignKey = @ForeignKey(name = "fk_deliveries_driver")
    )
    private DriverProfileEntity driver;

    @Column(name = "pickup_address", nullable = false, columnDefinition = "TEXT")
    private String pickupAddress;

    @Column(name = "dropoff_address", nullable = false, columnDefinition = "TEXT")
    private String dropoffAddress;

    @Column(name = "cod_amount", precision = 15, scale = 2)
    private BigDecimal codAmount;

    @Column(name = "delivery_fee", precision = 15, scale = 2)
    private BigDecimal deliveryFee;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private DeliveryStatus status;

    @Column(name = "pickup_time")
    private ZonedDateTime pickupTime;

    @Column(name = "dropoff_time")
    private ZonedDateTime dropoffTime;

    @Column(name = "reason_failed", columnDefinition = "TEXT")
    private String reasonFailed;

    @Column(name = "pickup_lat", precision = 10, scale = 8)
    private BigDecimal pickupLat;

    @Column(name = "pickup_lng", precision = 11, scale = 8)
    private BigDecimal pickupLng;

    @Column(name = "dropoff_lat", precision = 10, scale = 8)
    private BigDecimal dropoffLat;

    @Column(name = "dropoff_lng", precision = 11, scale = 8)
    private BigDecimal dropoffLng;

    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;
}