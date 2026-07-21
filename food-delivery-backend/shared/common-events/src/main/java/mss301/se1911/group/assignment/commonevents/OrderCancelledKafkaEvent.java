package mss301.se1911.group.assignment.commonevents;

import java.util.UUID;

public record OrderCancelledKafkaEvent(
    UUID orderId,
    String reason
) {}
