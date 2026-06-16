package mss301.se1911.group.assignment.orderservice.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import mss301.se1911.group.assignment.orderservice.application.command.RemoveItemCommand;
import mss301.se1911.group.assignment.orderservice.domain.aggregate.OrderAggregate;
import mss301.se1911.group.assignment.orderservice.domain.exception.OrderDomainException;
import mss301.se1911.group.assignment.orderservice.domain.repository.OrderRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class RemoveItemService implements RemoveItemUseCase {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse execute(RemoveItemCommand command) {
        log.info("Removing item: {} from order: {}", command.getProductId(), command.getOrderId());
        OrderAggregate order = orderRepository.findById(command.getOrderId())
                .orElseThrow(() -> new OrderDomainException("Order not found with id: " + command.getOrderId()));
        
        order.removeItem(command.getProductId());
        OrderAggregate savedOrder = orderRepository.save(order);
        return orderMapper.mapToResponse(savedOrder);
    }
}
