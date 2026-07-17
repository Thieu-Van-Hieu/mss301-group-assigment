package mss301.se1911.group.assignment.customerservice.domain.aggregate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.customerservice.domain.entity.AddressEntity;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class AddressAggregate {

    private final AddressEntity rootEntity;

    /**
     * Factory Method thêm địa chỉ giao hàng mới cho khách hàng.
     */
    public static AddressAggregate createNew(
            UUID customerId, String recipientName, String phoneNumber, String addressLine,
            String ward, String district, String city,
            BigDecimal latitude, BigDecimal longitude, boolean isDefault) {

        if (customerId == null) {
            throw new IllegalArgumentException("Địa chỉ phải thuộc về một khách hàng!");
        }
        if (addressLine == null || addressLine.isBlank()) {
            throw new IllegalArgumentException("Địa chỉ chi tiết không được để trống!");
        }

        AddressEntity entity = AddressEntity.builder()
                .id(UUID.randomUUID())
                .customerId(customerId)
                .recipientName(recipientName)
                .phoneNumber(phoneNumber)
                .addressLine(addressLine)
                .ward(ward)
                .district(district)
                .city(city)
                .latitude(latitude)
                .longitude(longitude)
                .isDefault(isDefault)
                .createdAt(ZonedDateTime.now())
                .build();

        return new AddressAggregate(entity);
    }

    /**
     * Sửa thông tin địa chỉ.
     */
    public void update(String recipientName, String phoneNumber, String addressLine,
                       String ward, String district, String city,
                       BigDecimal latitude, BigDecimal longitude) {
        if (addressLine != null && !addressLine.isBlank()) {
            this.rootEntity.setAddressLine(addressLine);
        }
        this.rootEntity.setRecipientName(recipientName);
        this.rootEntity.setPhoneNumber(phoneNumber);
        this.rootEntity.setWard(ward);
        this.rootEntity.setDistrict(district);
        this.rootEntity.setCity(city);
        this.rootEntity.setLatitude(latitude);
        this.rootEntity.setLongitude(longitude);
    }

    public void markAsDefault() {
        this.rootEntity.setDefault(true);
    }

    public void unmarkDefault() {
        this.rootEntity.setDefault(false);
    }
}
