package mss301.se1911.group.assignment.deliveryservice.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import mss301.se1911.group.assignment.deliveryservice.domain.entity.DeliveryEntity;
import mss301.se1911.group.assignment.deliveryservice.domain.entity.DriverProfileEntity;
import mss301.se1911.group.assignment.deliveryservice.domain.enums.DeliveryStatus;
import mss301.se1911.group.assignment.deliveryservice.infrastructure.persistence.JpaDeliveryRepository;
import mss301.se1911.group.assignment.deliveryservice.infrastructure.persistence.JpaDriverProfileRepository;
import mss301.se1911.group.assignment.commonevents.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaOrderPaidEventListener {

    private final JpaDeliveryRepository deliveryRepository;
    private final JpaDriverProfileRepository driverProfileRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "order-events-topic", groupId = "delivery-service-group")
    public void handleOrderPaidEvent(Object message) {
        log.info("Received event in delivery-service: {}", message);
        try {
            if (message instanceof OrderPaidKafkaEvent event) {
                log.info("Handling OrderPaidKafkaEvent for Order: {}, Address: {}", event.orderId(), event.address());

                // Simulated failure check: if address contains "fail" or "unknown"
                if (event.address() != null && (event.address().toLowerCase().contains("fail") || event.address().toLowerCase().contains("unknown"))) {
                    log.warn("Delivery assignment failed due to invalid address (simulated): {}", event.address());
                    
                    DeliveryFailedKafkaEvent failEvent = new DeliveryFailedKafkaEvent(
                            event.orderId(),
                            "Delivery address containing 'fail' or 'unknown' is invalid"
                    );
                    kafkaTemplate.send("delivery-events-topic", event.orderId().toString(), failEvent);
                    log.info("Published DeliveryFailedKafkaEvent to Kafka.");
                    return;
                }

                // Retrieve driver
                UUID seedDriverId = UUID.fromString("11111111-1111-1111-1111-111111111111");
                DriverProfileEntity driver = driverProfileRepository.findById(seedDriverId).orElse(null);
                if (driver == null) {
                    log.warn("No default driver found in db, creating one on the fly to avoid constraint error.");
                    driver = DriverProfileEntity.builder()
                            .driverId(seedDriverId)
                            .fullName("Fallback Seed Driver")
                            .licensePlate("29A-99999")
                            .vehicleType(mss301.se1911.group.assignment.deliveryservice.domain.enums.VehicleType.BIKE)
                            .online(true)
                            .status(mss301.se1911.group.assignment.deliveryservice.domain.enums.DriverStatus.AVAILABLE)
                            .createdAt(ZonedDateTime.now())
                            .build();
                    driverProfileRepository.save(driver);
                }

                // Create and save delivery record
                DeliveryEntity delivery = new DeliveryEntity();
                delivery.setId(UUID.randomUUID());
                delivery.setOrderId(event.orderId());
                delivery.setDriver(driver);
                delivery.setStatus(DeliveryStatus.ASSIGNED);
                delivery.setCreatedAt(ZonedDateTime.now());
                
                deliveryRepository.save(delivery);
                log.info("Delivery record saved in PostgreSQL with status ASSIGNED.");

                // Emit event to Kafka
                DeliveryCreatedKafkaEvent createdEvent = new DeliveryCreatedKafkaEvent(
                        event.orderId(),
                        delivery.getId(),
                        driver.getFullName(),
                        "0987654321",
                        "ASSIGNED"
                );
                kafkaTemplate.send("delivery-events-topic", event.orderId().toString(), createdEvent);
                log.info("Published DeliveryCreatedKafkaEvent to Kafka.");
            }
        } catch (Exception e) {
            log.error("Error processing OrderPaidKafkaEvent: {}", e.getMessage(), e);
        }
    }
}
