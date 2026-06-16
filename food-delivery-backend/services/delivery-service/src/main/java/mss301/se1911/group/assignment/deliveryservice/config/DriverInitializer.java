package mss301.se1911.group.assignment.deliveryservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import mss301.se1911.group.assignment.deliveryservice.domain.entity.DriverProfileEntity;
import mss301.se1911.group.assignment.deliveryservice.domain.enums.DriverStatus;
import mss301.se1911.group.assignment.deliveryservice.domain.enums.VehicleType;
import mss301.se1911.group.assignment.deliveryservice.infrastructure.persistence.JpaDriverProfileRepository;

import java.time.ZonedDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DriverInitializer implements ApplicationRunner {

    private final JpaDriverProfileRepository driverProfileRepository;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Checking driver profiles table...");
        if (driverProfileRepository.count() == 0) {
            log.info("No driver profiles found. Seeding a default driver profile...");
            
            DriverProfileEntity seedDriver = DriverProfileEntity.builder()
                    .driverId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                    .fullName("John Doe (Seed Driver)")
                    .licensePlate("29A-12345")
                    .vehicleType(VehicleType.BIKE)
                    .online(true)
                    .status(DriverStatus.AVAILABLE)
                    .createdAt(ZonedDateTime.now())
                    .build();
            
            driverProfileRepository.save(seedDriver);
            log.info("Default driver profile seeded successfully.");
        } else {
            log.info("Driver profiles exist. Seeding skipped.");
        }
    }
}
