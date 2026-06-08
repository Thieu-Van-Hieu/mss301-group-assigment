package mss301.se1911.group.assignment.deliveryservice.infrastructure.messaging.kafka.event;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserOnboardingEvent(
        UUID id,
        String fullName,
        String phoneNumber,
        String email,
        String role // "DRIVER", "CUSTOMER", "MERCHANT"
) {}