package mss301.se1911.group.assignment.orderservice.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import mss301.se1911.group.assignment.orderservice.application.command.AddOrUpdateItemCommand;
import mss301.se1911.group.assignment.orderservice.domain.aggregate.OrderAggregate;
import mss301.se1911.group.assignment.orderservice.domain.aggregate.OrderItem;
import mss301.se1911.group.assignment.orderservice.domain.exception.OrderDomainException;
import mss301.se1911.group.assignment.orderservice.domain.repository.OrderRepository;
import mss301.se1911.group.assignment.orderservice.domain.vo.Money;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddOrUpdateItemService implements AddOrUpdateItemUseCase {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse execute(AddOrUpdateItemCommand command) {
        log.info("Adding/Updating item for order: {}", command.getOrderId());
        OrderAggregate order = orderRepository.findById(command.getOrderId())
                .orElseThrow(() -> new OrderDomainException("Order not found with id: " + command.getOrderId()));
        
        OrderItem newItem = new OrderItem(
                command.getItemDto().getProductId(),
                command.getItemDto().getName(),
                command.getItemDto().getQuantity(),
                new Money(command.getItemDto().getPrice(), command.getItemDto().getCurrency() != null ? command.getItemDto().getCurrency() : "VND")
        );
        
        order.addOrUpdateItem(newItem);
        OrderAggregate savedOrder = orderRepository.save(order);
        return orderMapper.mapToResponse(savedOrder);
    }
}
