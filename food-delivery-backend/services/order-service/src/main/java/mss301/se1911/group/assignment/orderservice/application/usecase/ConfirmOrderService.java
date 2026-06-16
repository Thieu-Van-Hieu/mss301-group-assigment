package mss301.se1911.group.assignment.orderservice.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import mss301.se1911.group.assignment.orderservice.application.command.ConfirmOrderCommand;
import mss301.se1911.group.assignment.orderservice.domain.aggregate.OrderAggregate;
import mss301.se1911.group.assignment.orderservice.domain.aggregate.OrderConfirmedEvent;
import mss301.se1911.group.assignment.orderservice.domain.exception.OrderDomainException;
import mss301.se1911.group.assignment.orderservice.domain.repository.OrderRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfirmOrderService implements ConfirmOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse execute(ConfirmOrderCommand command) {
        log.info("Confirming order: {}", command.getOrderId());
        OrderAggregate order = orderRepository.findById(command.getOrderId())
                .orElseThrow(() -> new OrderDomainException("Order not found with id: " + command.getOrderId()));
        
        order.confirm();
        OrderAggregate savedOrder = orderRepository.save(order);
        eventPublisher.publish(new OrderConfirmedEvent(savedOrder));
        return orderMapper.mapToResponse(savedOrder);
    }
}
