package mss301.se1911.group.assignment.orderservice.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import mss301.se1911.group.assignment.orderservice.domain.aggregate.OrderAggregate;
import mss301.se1911.group.assignment.orderservice.domain.exception.OrderDomainException;
import mss301.se1911.group.assignment.orderservice.domain.repository.OrderRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetOrderByIdService implements GetOrderByIdUseCase {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse execute(UUID id) {
        OrderAggregate order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderDomainException("Order not found with id: " + id));
        return orderMapper.mapToResponse(order);
    }
}
