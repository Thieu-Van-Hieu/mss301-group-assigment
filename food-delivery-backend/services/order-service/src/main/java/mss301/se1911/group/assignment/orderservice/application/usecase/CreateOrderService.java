package mss301.se1911.group.assignment.orderservice.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import mss301.se1911.group.assignment.orderservice.application.command.CreateOrderCommand;
import mss301.se1911.group.assignment.orderservice.domain.aggregate.*;
import mss301.se1911.group.assignment.orderservice.domain.exception.OrderDomainException;
import mss301.se1911.group.assignment.orderservice.domain.repository.OrderRepository;
import mss301.se1911.group.assignment.orderservice.domain.vo.*;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateOrderService implements CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final CustomerServicePort customerServicePort;
    private final RestaurantServicePort restaurantServicePort;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse execute(CreateOrderCommand command) {
        log.info("Creating order for customer: {}, restaurant: {}", command.getCustomerId(), command.getRestaurantId());
        
        customerServicePort.validateCustomer(command.getCustomerId());
        
        if (command.getItems() != null && !command.getItems().isEmpty()) {
            restaurantServicePort.validateRestaurantAndItems(command.getRestaurantId(), command.getItems());
        } else {
            throw new OrderDomainException("Cannot create an order with no items");
        }
        
        List<OrderItem> domainItems = command.getItems().stream()
                .map(dto -> new OrderItem(
                        dto.getProductId(),
                        dto.getName(),
                        dto.getQuantity(),
                        new Money(dto.getPrice(), dto.getCurrency() != null ? dto.getCurrency() : "VND")
                )).toList();
                
        DeliveryInfo deliveryInfo = null;
        if (command.getDeliveryInfo() != null) {
            deliveryInfo = new DeliveryInfo(
                    command.getDeliveryInfo().getAddress(),
                    command.getDeliveryInfo().getLatitude(),
                    command.getDeliveryInfo().getLongitude(),
                    command.getDeliveryInfo().getPhone()
            );
        }
        
        PaymentInfo paymentInfo = null;
        if (command.getPaymentInfo() != null) {
            paymentInfo = new PaymentInfo(
                    command.getPaymentInfo().getMethod(),
                    command.getPaymentInfo().getStatus() != null ? command.getPaymentInfo().getStatus() : PaymentStatus.PENDING,
                    command.getPaymentInfo().getTransactionId()
            );
        }

        OrderAggregate order = OrderAggregate.builder()
                .customerId(command.getCustomerId())
                .restaurantId(command.getRestaurantId())
                .items(domainItems)
                .deliveryInfo(deliveryInfo)
                .paymentInfo(paymentInfo)
                .status(OrderStatus.CREATED)
                .build();
                
        OrderAggregate savedOrder = orderRepository.save(order);
        eventPublisher.publish(new OrderCreatedEvent(savedOrder));
        return orderMapper.mapToResponse(savedOrder);
    }
}
