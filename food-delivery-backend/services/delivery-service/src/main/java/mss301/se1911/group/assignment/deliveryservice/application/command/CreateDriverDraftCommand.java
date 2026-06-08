package mss301.se1911.group.assignment.deliveryservice.application.command;

import java.util.UUID;

public record CreateDriverDraftCommand(
        UUID driverId,
        String fullName,
        String phoneNumber,
        String email
) {}
