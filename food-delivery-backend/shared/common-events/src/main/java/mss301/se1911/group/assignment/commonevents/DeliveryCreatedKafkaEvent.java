package mss301.se1911.group.assignment.commonevents;

import java.util.UUID;

public record DeliveryCreatedKafkaEvent(
    UUID orderId,
    UUID deliveryId,
    String driverName,
    String driverPhone,
    String status
) {}
