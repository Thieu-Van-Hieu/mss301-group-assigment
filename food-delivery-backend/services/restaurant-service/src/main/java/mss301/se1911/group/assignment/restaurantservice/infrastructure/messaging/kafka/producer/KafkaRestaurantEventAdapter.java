package mss301.se1911.group.assignment.restaurantservice.infrastructure.messaging.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.restaurantservice.domain.aggregate.RestaurantAggregate;
import mss301.se1911.group.assignment.restaurantservice.domain.entity.RestaurantEntity;
import mss301.se1911.group.assignment.restaurantservice.domain.enums.RestaurantStatus;
import mss301.se1911.group.assignment.restaurantservice.domain.event.RestaurantEventPublisher;
import mss301.se1911.group.assignment.restaurantservice.infrastructure.messaging.kafka.event.MenuUpdatedEvent;
import mss301.se1911.group.assignment.restaurantservice.infrastructure.messaging.kafka.event.RestaurantCreatedEvent;
import mss301.se1911.group.assignment.restaurantservice.infrastructure.messaging.kafka.event.RestaurantStatusChangedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaRestaurantEventAdapter implements RestaurantEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.restaurant-created}")
    private String restaurantCreatedTopic;

    @Value("${app.kafka.topics.menu-updated}")
    private String menuUpdatedTopic;

    @Value("${app.kafka.topics.restaurant-status-changed}")
    private String restaurantStatusChangedTopic;

    @Override
    public void publishRestaurantCreated(RestaurantAggregate restaurant) {
        RestaurantEntity entity = restaurant.getRootEntity();

        RestaurantCreatedEvent event = RestaurantCreatedEvent.builder()
                .eventId(UUID.randomUUID())
                .restaurantId(entity.getId())
                .ownerId(entity.getOwnerId())
                .name(entity.getName())
                .cuisineType(entity.getCuisineType())
                .timestamp(ZonedDateTime.now())
                .build();

        send(restaurantCreatedTopic, entity.getId().toString(), event);
    }

    @Override
    public void publishMenuUpdated(UUID restaurantId, UUID menuItemId, String action) {
        MenuUpdatedEvent event = MenuUpdatedEvent.builder()
                .eventId(UUID.randomUUID())
                .restaurantId(restaurantId)
                .menuItemId(menuItemId)
                .action(action)
                .timestamp(ZonedDateTime.now())
                .build();

        send(menuUpdatedTopic, restaurantId.toString(), event);
    }

    @Override
    public void publishRestaurantStatusChanged(UUID restaurantId, RestaurantStatus oldStatus, RestaurantStatus newStatus) {
        RestaurantStatusChangedEvent event = RestaurantStatusChangedEvent.builder()
                .eventId(UUID.randomUUID())
                .restaurantId(restaurantId)
                .oldStatus(oldStatus != null ? oldStatus.name() : null)
                .newStatus(newStatus != null ? newStatus.name() : null)
                .timestamp(ZonedDateTime.now())
                .build();

        send(restaurantStatusChangedTopic, restaurantId.toString(), event);
    }

    /**
     * Gửi event dạng JSON sang Kafka, dùng restaurantId làm Message Key để đảm bảo tính tuần tự theo từng nhà hàng.
     * Kafka lỗi không được làm sập luồng nghiệp vụ chính -> chỉ log lại (thực tế nên đẩy vào Retry/DLT).
     */
    private void send(String topic, String key, Object event) {
        try {
            kafkaTemplate.send(topic, key, event);
            log.info("Đã phát sự kiện [{}] tới topic [{}] với key [{}]", event.getClass().getSimpleName(), topic, key);
        } catch (Exception e) {
            log.error("Phát sự kiện tới topic [{}] thất bại: {}", topic, e.getMessage(), e);
        }
    }
}
