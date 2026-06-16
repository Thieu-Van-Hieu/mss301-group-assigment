package mss301.se1911.group.assignment.orderservice.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import mss301.se1911.group.assignment.orderservice.application.command.CancelOrderCommand;
import mss301.se1911.group.assignment.orderservice.domain.aggregate.OrderAggregate;
import mss301.se1911.group.assignment.orderservice.domain.aggregate.OrderCancelledEvent;
import mss301.se1911.group.assignment.orderservice.domain.exception.OrderDomainException;
import mss301.se1911.group.assignment.orderservice.domain.repository.OrderRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CancelOrderService implements CancelOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse execute(CancelOrderCommand command) {
        log.info("Cancelling order: {} with reason: {}", command.getOrderId(), command.getReason());
        OrderAggregate order = orderRepository.findById(command.getOrderId())
                .orElseThrow(() -> new OrderDomainException("Order not found with id: " + command.getOrderId()));
        
        order.cancel(command.getReason());
        OrderAggregate savedOrder = orderRepository.save(order);
        eventPublisher.publish(new OrderCancelledEvent(savedOrder, command.getReason()));
        return orderMapper.mapToResponse(savedOrder);
    }
}
