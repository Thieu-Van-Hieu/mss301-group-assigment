package mss301.se1911.group.assignment.restaurantservice.application.usecase;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.restaurantservice.application.command.UpdateRestaurantCommand;
import mss301.se1911.group.assignment.restaurantservice.application.dto.RestaurantResponse;
import mss301.se1911.group.assignment.restaurantservice.domain.aggregate.RestaurantAggregate;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateRestaurantUseCase {

    private final RestaurantRepository restaurantRepository;

    @Transactional
    public RestaurantResponse execute(UpdateRestaurantCommand command) {
        RestaurantAggregate aggregate = restaurantRepository.findById(command.restaurantId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhà hàng với ID: " + command.restaurantId()));

        aggregate.updateInfo(
                command.name(),
                command.address(),
                command.imageUrl(),
                command.description(),
                command.cuisineType(),
                command.openingTime(),
                command.closingTime()
        );

        restaurantRepository.save(aggregate);

        return RestaurantResponse.fromAggregate(aggregate);
    }
}
