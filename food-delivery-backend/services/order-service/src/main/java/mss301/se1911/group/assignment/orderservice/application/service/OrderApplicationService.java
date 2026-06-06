package mss301.se1911.group.assignment.orderservice.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import mss301.se1911.group.assignment.orderservice.application.dto.*;
import mss301.se1911.group.assignment.orderservice.application.ports.in.OrderUseCase;
import mss301.se1911.group.assignment.orderservice.application.ports.out.OrderEventPublisher;
import mss301.se1911.group.assignment.orderservice.domain.event.*;
import mss301.se1911.group.assignment.orderservice.domain.exception.OrderDomainException;
import mss301.se1911.group.assignment.orderservice.domain.model.*;
import mss301.se1911.group.assignment.orderservice.domain.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderApplicationService implements OrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    @Override
    public OrderResponse createOrder(CreateOrderCommand command) {
        log.info("Creating order for customer: {}, restaurant: {}", command.getCustomerId(), command.getRestaurantId());
        
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

        Order order = Order.builder()
                .customerId(command.getCustomerId())
                .restaurantId(command.getRestaurantId())
                .items(domainItems)
                .deliveryInfo(deliveryInfo)
                .paymentInfo(paymentInfo)
                .status(OrderStatus.CREATED)
                .build();
                
        Order savedOrder = orderRepository.save(order);
        
        eventPublisher.publish(new OrderCreatedEvent(savedOrder));
        
        return mapToResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrderById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderDomainException("Order not found with id: " + id));
        return mapToResponse(order);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public OrderResponse addOrUpdateItem(UUID id, OrderItemDto itemDto) {
        log.info("Adding/Updating item for order: {}", id);
        Order order = getOrder(id);
        
        OrderItem newItem = new OrderItem(
                itemDto.getProductId(),
                itemDto.getName(),
                itemDto.getQuantity(),
                new Money(itemDto.getPrice(), itemDto.getCurrency() != null ? itemDto.getCurrency() : "VND")
        );
        
        order.addOrUpdateItem(newItem);
        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    @Override
    public OrderResponse removeItem(UUID id, UUID productId) {
        log.info("Removing item: {} from order: {}", productId, id);
        Order order = getOrder(id);
        order.removeItem(productId);
        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    @Override
    public OrderResponse updateDeliveryInfo(UUID id, DeliveryInfoDto deliveryInfoDto) {
        log.info("Updating delivery info for order: {}", id);
        Order order = getOrder(id);
        
        DeliveryInfo deliveryInfo = new DeliveryInfo(
                deliveryInfoDto.getAddress(),
                deliveryInfoDto.getLatitude(),
                deliveryInfoDto.getLongitude(),
                deliveryInfoDto.getPhone()
        );
        
        order.updateDeliveryInfo(deliveryInfo);
        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    @Override
    public OrderResponse updatePaymentInfo(UUID id, PaymentInfoDto paymentInfoDto) {
        log.info("Updating payment info for order: {}", id);
        Order order = getOrder(id);
        
        PaymentInfo paymentInfo = new PaymentInfo(
                paymentInfoDto.getMethod(),
                paymentInfoDto.getStatus(),
                paymentInfoDto.getTransactionId()
        );
        
        order.updatePaymentInfo(paymentInfo);
        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    @Override
    public OrderResponse payOrder(UUID id) {
        log.info("Paying order: {}", id);
        Order order = getOrder(id);
        order.pay();
        Order savedOrder = orderRepository.save(order);
        eventPublisher.publish(new OrderPaidEvent(savedOrder));
        return mapToResponse(savedOrder);
    }

    @Override
    public OrderResponse confirmOrder(UUID id) {
        log.info("Confirming order: {}", id);
        Order order = getOrder(id);
        order.confirm();
        Order savedOrder = orderRepository.save(order);
        eventPublisher.publish(new OrderConfirmedEvent(savedOrder));
        return mapToResponse(savedOrder);
    }

    @Override
    public OrderResponse updateOrderStatus(UUID id, OrderStatus status) {
        log.info("Transitioning status of order: {} to {}", id, status);
        Order order = getOrder(id);
        
        switch (status) {
            case CONFIRMED -> order.confirm();
            case PREPARING -> order.startPreparing();
            case OUT_FOR_DELIVERY -> order.startDelivery();
            case DELIVERED -> order.deliver();
            default -> throw new OrderDomainException("Unsupported transition trigger directly: " + status);
        }
        
        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    @Override
    public OrderResponse cancelOrder(UUID id, String reason) {
        log.info("Cancelling order: {} with reason: {}", id, reason);
        Order order = getOrder(id);
        order.cancel(reason);
        Order savedOrder = orderRepository.save(order);
        eventPublisher.publish(new OrderCancelledEvent(savedOrder, reason));
        return mapToResponse(savedOrder);
    }

    private Order getOrder(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderDomainException("Order not found with id: " + id));
    }

    private OrderResponse mapToResponse(Order order) {
        if (order == null) return null;
        
        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> OrderItemDto.builder()
                        .productId(item.getProductId())
                        .name(item.getName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice().getAmount())
                        .currency(item.getPrice().getCurrency())
                        .build())
                .toList();
                
        DeliveryInfoDto deliveryInfoDto = null;
        if (order.getDeliveryInfo() != null) {
            deliveryInfoDto = DeliveryInfoDto.builder()
                    .address(order.getDeliveryInfo().getAddress())
                    .latitude(order.getDeliveryInfo().getLatitude())
                    .longitude(order.getDeliveryInfo().getLongitude())
                    .phone(order.getDeliveryInfo().getPhone())
                    .build();
        }
        
        PaymentInfoDto paymentInfoDto = null;
        if (order.getPaymentInfo() != null) {
            paymentInfoDto = PaymentInfoDto.builder()
                    .method(order.getPaymentInfo().getMethod())
                    .status(order.getPaymentInfo().getStatus())
                    .transactionId(order.getPaymentInfo().getTransactionId())
                    .build();
        }
        
        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .restaurantId(order.getRestaurantId())
                .items(itemDtos)
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount().getAmount())
                .currency(order.getTotalAmount().getCurrency())
                .deliveryInfo(deliveryInfoDto)
                .paymentInfo(paymentInfoDto)
                .createdAt(order.getCreatedAt())
                .build();
    }
}
