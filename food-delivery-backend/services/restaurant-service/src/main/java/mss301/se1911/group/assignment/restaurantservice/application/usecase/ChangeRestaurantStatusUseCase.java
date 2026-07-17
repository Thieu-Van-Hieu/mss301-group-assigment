package mss301.se1911.group.assignment.restaurantservice.application.usecase;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.restaurantservice.application.dto.RestaurantResponse;
import mss301.se1911.group.assignment.restaurantservice.domain.aggregate.RestaurantAggregate;
import mss301.se1911.group.assignment.restaurantservice.domain.enums.RestaurantStatus;
import mss301.se1911.group.assignment.restaurantservice.domain.event.RestaurantEventPublisher;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChangeRestaurantStatusUseCase {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantEventPublisher eventPublisher;

    @Transactional
    public RestaurantResponse execute(UUID restaurantId, RestaurantStatus newStatus) {
        RestaurantAggregate aggregate = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhà hàng với ID: " + restaurantId));

        RestaurantStatus oldStatus = aggregate.changeStatus(newStatus);
        restaurantRepository.save(aggregate);

        // Chỉ phát sự kiện khi trạng thái thực sự thay đổi
        if (oldStatus != newStatus) {
            eventPublisher.publishRestaurantStatusChanged(restaurantId, oldStatus, newStatus);
        }

        return RestaurantResponse.fromAggregate(aggregate);
    }
}
