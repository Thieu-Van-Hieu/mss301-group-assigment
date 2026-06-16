package mss301.se1911.group.assignment.orderservice.infrastructure.feign.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import mss301.se1911.group.assignment.orderservice.application.usecase.CustomerServicePort;
import mss301.se1911.group.assignment.orderservice.infrastructure.feign.client.CustomerFeignClient;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerServiceFeignAdapter implements CustomerServicePort {

    private final CustomerFeignClient customerFeignClient;

    @Override
    public void validateCustomer(UUID customerId) {
        log.info("Checking customer validity for ID: {}", customerId);
        customerFeignClient.validateCustomer(customerId);
    }
}
