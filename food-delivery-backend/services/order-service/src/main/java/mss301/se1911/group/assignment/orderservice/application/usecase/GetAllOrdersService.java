package mss301.se1911.group.assignment.orderservice.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import mss301.se1911.group.assignment.orderservice.domain.repository.OrderRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetAllOrdersService implements GetAllOrdersUseCase {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public List<OrderResponse> execute() {
        return orderRepository.findAll().stream()
                .map(orderMapper::mapToResponse)
                .toList();
    }
}
