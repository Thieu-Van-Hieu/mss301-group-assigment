package mss301.se1911.group.assignment.orderservice.infrastructure.feign.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import mss301.se1911.group.assignment.orderservice.application.command.OrderItemDto;
import mss301.se1911.group.assignment.orderservice.application.usecase.RestaurantServicePort;
import mss301.se1911.group.assignment.orderservice.infrastructure.feign.client.RestaurantFeignClient;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantServiceFeignAdapter implements RestaurantServicePort {

    private final RestaurantFeignClient restaurantFeignClient;

    @Override
    public void validateRestaurantAndItems(UUID restaurantId, List<OrderItemDto> items) {
        log.info("Checking restaurant menu validity for Restaurant ID: {}", restaurantId);
        // TODO: Gọi sang restaurant service qua Feign Client để check thông tin món ăn và giá
        // restaurantFeignClient.validateMenu(restaurantId, items);
    }
}
