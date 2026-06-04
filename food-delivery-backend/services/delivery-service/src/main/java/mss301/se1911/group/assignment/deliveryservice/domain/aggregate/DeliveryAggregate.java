package mss301.se1911.group.assignment.deliveryservice.domain.aggregate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.deliveryservice.domain.entity.DeliveryEntity;
import mss301.se1911.group.assignment.deliveryservice.domain.entity.DriverProfileEntity;
import mss301.se1911.group.assignment.deliveryservice.domain.enums.DeliveryStatus;

@Getter
@RequiredArgsConstructor
public class DeliveryAggregate {

    private final DeliveryEntity delivery;

    public void acceptByDriver(DriverProfileAggregate driverAggregate) {
        if (DeliveryStatus.ASSIGNED != this.delivery.getStatus()) {
            throw new IllegalStateException("Delivery is not in ASSIGNED status");
        }

        driverAggregate.validateEligibleForDelivery();

        this.delivery.setStatus(DeliveryStatus.PICKING_UP);
        driverAggregate.occupyDriver();
    }
}
