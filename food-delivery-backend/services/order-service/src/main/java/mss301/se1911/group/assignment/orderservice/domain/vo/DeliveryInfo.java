package mss301.se1911.group.assignment.orderservice.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import mss301.se1911.group.assignment.orderservice.domain.exception.OrderDomainException;

@Getter
@EqualsAndHashCode
@ToString
public class DeliveryInfo {
    private final String address;
    private final double latitude;
    private final double longitude;
    private final String phone;

    public DeliveryInfo(String address, double latitude, double longitude, String phone) {
        if (address == null || address.trim().isEmpty()) {
            throw new OrderDomainException("Delivery address cannot be empty");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new OrderDomainException("Delivery phone number cannot be empty");
        }
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
    }
}
