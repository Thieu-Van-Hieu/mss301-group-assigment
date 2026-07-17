package mss301.se1911.group.assignment.restaurantservice.application.usecase;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.restaurantservice.application.command.CreateRestaurantCommand;
import mss301.se1911.group.assignment.restaurantservice.application.dto.RestaurantResponse;
import mss301.se1911.group.assignment.restaurantservice.domain.aggregate.RestaurantAggregate;
import mss301.se1911.group.assignment.restaurantservice.domain.event.RestaurantEventPublisher;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateRestaurantUseCase {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantEventPublisher eventPublisher;

    @Transactional
    public RestaurantResponse execute(CreateRestaurantCommand command) {
        RestaurantAggregate aggregate = RestaurantAggregate.createNewRestaurant(
                command.ownerId(),
                command.name(),
                command.address(),
                command.imageUrl(),
                command.description(),
                command.cuisineType(),
                command.openingTime(),
                command.closingTime()
        );

        restaurantRepository.save(aggregate);

        // Phát sự kiện RestaurantCreated cho các service khác (search, order...) đồng bộ dữ liệu
        eventPublisher.publishRestaurantCreated(aggregate);

        return RestaurantResponse.fromAggregate(aggregate);
    }
}
