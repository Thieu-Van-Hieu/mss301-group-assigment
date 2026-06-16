package mss301.se1911.group.assignment.commonevents;

import java.util.UUID;

public record DeliveryFailedKafkaEvent(
    UUID orderId,
    String reason
) {}
