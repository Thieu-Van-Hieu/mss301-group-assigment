package mss301.se1911.group.assignment.orderservice.infrastructure.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import mss301.se1911.group.assignment.orderservice.infrastructure.feign.config.FeignClientConfig;
import mss301.se1911.group.assignment.orderservice.application.command.OrderItemDto;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "restaurant-service", configuration = FeignClientConfig.class)
public interface RestaurantFeignClient {

    @PostMapping("/api/restaurants/{id}/validate-menu")
    void validateMenu(@PathVariable("id") UUID restaurantId, @RequestBody List<OrderItemDto> items);
}
