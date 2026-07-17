package mss301.se1911.group.assignment.customerservice.application.dto;

import mss301.se1911.group.assignment.customerservice.domain.aggregate.AddressAggregate;
import mss301.se1911.group.assignment.customerservice.domain.entity.AddressEntity;

import java.math.BigDecimal;
import java.util.UUID;

public record AddressResponse(
        UUID id,
        UUID customerId,
        String recipientName,
        String phoneNumber,
        String addressLine,
        String ward,
        String district,
        String city,
        BigDecimal latitude,
        BigDecimal longitude,
        boolean isDefault
) {
    public static AddressResponse fromAggregate(AddressAggregate aggregate) {
        if (aggregate == null) return null;
        AddressEntity e = aggregate.getRootEntity();
        return new AddressResponse(
                e.getId(), e.getCustomerId(), e.getRecipientName(), e.getPhoneNumber(),
                e.getAddressLine(), e.getWard(), e.getDistrict(), e.getCity(),
                e.getLatitude(), e.getLongitude(), e.isDefault()
        );
    }
}
