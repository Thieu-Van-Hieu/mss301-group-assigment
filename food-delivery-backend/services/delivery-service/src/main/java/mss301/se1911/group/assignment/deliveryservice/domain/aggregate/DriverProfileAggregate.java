package mss301.se1911.group.assignment.deliveryservice.domain.aggregate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.deliveryservice.domain.entity.DriverProfileEntity;
import mss301.se1911.group.assignment.deliveryservice.domain.enums.DriverStatus;

@Getter
@RequiredArgsConstructor
public class DriverProfileAggregate {

    private final DriverProfileEntity driverProfile;

    public void validateEligibleForDelivery() {
        if ( !driverProfile.isOnline()) {
            throw new IllegalStateException("Driver is not online");
        }
        if (driverProfile.getStatus() == DriverStatus.BUSY) {
            throw new IllegalStateException("Driver is currently busy");
        }
    }

    public void occupyDriver() {
        validateEligibleForDelivery();
        this.driverProfile.setStatus(DriverStatus.BUSY);
    }
}
