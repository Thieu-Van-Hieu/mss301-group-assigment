package mss301.se1911.group.assignment.deliveryservice.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.deliveryservice.domain.aggregate.DriverProfileAggregate;
import mss301.se1911.group.assignment.deliveryservice.domain.entity.DriverProfileEntity;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.DriverProfileRepository;
import mss301.se1911.group.assignment.deliveryservice.infrastructure.persistence.JpaDriverProfileRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DriverProfileRepositoryImpl implements DriverProfileRepository {

    private final JpaDriverProfileRepository jpaDriverProfileRepository;

    @Override
    public Optional<DriverProfileAggregate> findById(UUID driver) {
        DriverProfileEntity driverProfileEntity = jpaDriverProfileRepository.findById(driver).orElse(null);
        if (driverProfileEntity == null) {
            return Optional.empty();
        }
        DriverProfileAggregate driverProfileAggregate = new DriverProfileAggregate(driverProfileEntity);
        return Optional.of(driverProfileAggregate);
    }

    @Override
    public void save(DriverProfileAggregate driverProfile) {
        DriverProfileEntity driverProfileEntity = driverProfile.getDriverProfile();

        jpaDriverProfileRepository.save(driverProfileEntity);
    }
}