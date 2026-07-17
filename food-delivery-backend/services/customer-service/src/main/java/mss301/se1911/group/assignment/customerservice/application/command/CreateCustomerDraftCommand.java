package mss301.se1911.group.assignment.customerservice.application.command;

import java.util.UUID;

public record CreateCustomerDraftCommand(
        UUID userId,
        String fullName,
        String email,
        String phoneNumber
) {}
