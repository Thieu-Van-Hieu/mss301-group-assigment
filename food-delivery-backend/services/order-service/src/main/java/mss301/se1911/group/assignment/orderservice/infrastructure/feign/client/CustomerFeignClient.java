package mss301.se1911.group.assignment.orderservice.infrastructure.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import mss301.se1911.group.assignment.orderservice.infrastructure.feign.config.FeignClientConfig;

import java.util.UUID;

@FeignClient(name = "customer-service", configuration = FeignClientConfig.class)
public interface CustomerFeignClient {

    @GetMapping("/api/customers/{id}/validate")
    void validateCustomer(@PathVariable("id") UUID id);
}
