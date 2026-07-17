package mss301.se1911.group.assignment.customerservice.application.dto;

import mss301.se1911.group.assignment.customerservice.domain.aggregate.CustomerAggregate;
import mss301.se1911.group.assignment.customerservice.domain.entity.CustomerEntity;

import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String fullName,
        String email,
        String phoneNumber
) {
    public static CustomerResponse fromAggregate(CustomerAggregate aggregate) {
        if (aggregate == null) return null;
        CustomerEntity e = aggregate.getRootEntity();
        return new CustomerResponse(e.getId(), e.getFullName(), e.getEmail(), e.getPhoneNumber());
    }
}
