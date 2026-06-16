package mss301.se1911.group.assignment.orderservice.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import mss301.se1911.group.assignment.orderservice.application.command.UpdateOrderStatusCommand;
import mss301.se1911.group.assignment.orderservice.application.usecase.OrderMapper;
import mss301.se1911.group.assignment.orderservice.domain.aggregate.OrderAggregate;
import mss301.se1911.group.assignment.orderservice.domain.aggregate.OrderConfirmedEvent;
import mss301.se1911.group.assignment.orderservice.domain.exception.OrderDomainException;
import mss301.se1911.group.assignment.orderservice.domain.repository.OrderRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateOrderStatusService implements UpdateOrderStatusUseCase {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse execute(UpdateOrderStatusCommand command) {
        log.info("Transitioning status of order: {} to {}", command.getOrderId(), command.getStatus());
        OrderAggregate order = orderRepository.findById(command.getOrderId())
                .orElseThrow(() -> new OrderDomainException("Order not found with id: " + command.getOrderId()));
        
        switch (command.getStatus()) {
            case CONFIRMED -> {
                order.confirm();
                OrderAggregate savedOrder = orderRepository.save(order);
                eventPublisher.publish(new OrderConfirmedEvent(savedOrder));
                return orderMapper.mapToResponse(savedOrder);
            }
            case PREPARING -> order.startPreparing();
            case OUT_FOR_DELIVERY -> order.startDelivery();
            case DELIVERED -> order.deliver();
            default -> throw new OrderDomainException("Unsupported transition trigger directly: " + command.getStatus());
        }
        
        OrderAggregate savedOrder = orderRepository.save(order);
        return orderMapper.mapToResponse(savedOrder);
    }
}
