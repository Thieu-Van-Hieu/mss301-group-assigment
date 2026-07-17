package mss301.se1911.group.assignment.customerservice.api.dto.request;

import mss301.se1911.group.assignment.customerservice.application.command.UpdateCustomerCommand;

import java.util.UUID;

public record UpdateCustomerRequest(
        String fullName,
        String email,
        String phoneNumber
) {
    public UpdateCustomerCommand toCommand(UUID customerId) {
        return new UpdateCustomerCommand(customerId, this.fullName, this.email, this.phoneNumber);
    }
}
