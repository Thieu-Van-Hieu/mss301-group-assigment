package mss301.se1911.group.assignment.restaurantservice.domain.event;

import mss301.se1911.group.assignment.restaurantservice.domain.aggregate.RestaurantAggregate;
import mss301.se1911.group.assignment.restaurantservice.domain.enums.RestaurantStatus;

import java.util.UUID;

/**
 * Cổng (Port) phát sự kiện nghiệp vụ của Restaurant ra hệ thống (Kafka).
 * Tầng domain/application chỉ phụ thuộc vào interface này, không biết chi tiết hạ tầng.
 */
public interface RestaurantEventPublisher {

    void publishRestaurantCreated(RestaurantAggregate restaurant);

    void publishMenuUpdated(UUID restaurantId, UUID menuItemId, String action);

    void publishRestaurantStatusChanged(UUID restaurantId, RestaurantStatus oldStatus, RestaurantStatus newStatus);
}
