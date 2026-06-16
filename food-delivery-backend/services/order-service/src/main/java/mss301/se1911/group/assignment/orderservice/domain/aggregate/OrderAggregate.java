package mss301.se1911.group.assignment.orderservice.domain.aggregate;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import mss301.se1911.group.assignment.orderservice.domain.exception.OrderDomainException;
import mss301.se1911.group.assignment.orderservice.domain.vo.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
public class OrderAggregate {
    private final UUID id;
    private final UUID customerId;
    private final UUID restaurantId;
    private final List<OrderItem> items;
    private OrderStatus status;
    private Money totalAmount;
    private DeliveryInfo deliveryInfo;
    private PaymentInfo paymentInfo;
    private final LocalDateTime createdAt;

    @Builder
    public OrderAggregate(UUID id, UUID customerId, UUID restaurantId, List<OrderItem> items, 
                  OrderStatus status, DeliveryInfo deliveryInfo, PaymentInfo paymentInfo, 
                  LocalDateTime createdAt) {
        this.id = id != null ? id : UUID.randomUUID();
        
        if (customerId == null) {
            throw new OrderDomainException("Customer ID cannot be null");
        }
        if (restaurantId == null) {
            throw new OrderDomainException("Restaurant ID cannot be null");
        }
        
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.status = status != null ? status : OrderStatus.CREATED;
        this.deliveryInfo = deliveryInfo;
        this.paymentInfo = paymentInfo;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        
        recalculateTotalAmount();
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addOrUpdateItem(OrderItem newItem) {
        validateItemsModifiable();
        if (newItem == null) {
            throw new OrderDomainException("Order item cannot be null");
        }
        
        boolean exists = false;
        for (OrderItem item : items) {
            if (item.getProductId().equals(newItem.getProductId())) {
                item.updateQuantity(item.getQuantity() + newItem.getQuantity());
                exists = true;
                break;
            }
        }
        if (!exists) {
            items.add(newItem);
        }
        
        recalculateTotalAmount();
    }

    public void removeItem(UUID productId) {
        validateItemsModifiable();
        if (productId == null) {
            throw new OrderDomainException("Product ID cannot be null");
        }
        boolean removed = items.removeIf(item -> item.getProductId().equals(productId));
        if (!removed) {
            throw new OrderDomainException("Item not found in order: " + productId);
        }
        
        recalculateTotalAmount();
    }

    public void updateDeliveryInfo(DeliveryInfo deliveryInfo) {
        if (status == OrderStatus.OUT_FOR_DELIVERY || status == OrderStatus.DELIVERED || status == OrderStatus.CANCELLED) {
            throw new OrderDomainException("Cannot update delivery info when order is " + status);
        }
        if (deliveryInfo == null) {
            throw new OrderDomainException("Delivery info cannot be null");
        }
        this.deliveryInfo = deliveryInfo;
    }

    public void updatePaymentInfo(PaymentInfo paymentInfo) {
        if (status == OrderStatus.DELIVERED || status == OrderStatus.CANCELLED) {
            throw new OrderDomainException("Cannot update payment info when order is " + status);
        }
        if (paymentInfo == null) {
            throw new OrderDomainException("Payment info cannot be null");
        }
        this.paymentInfo = paymentInfo;
    }

    public void pay() {
        if (status != OrderStatus.CREATED && status != OrderStatus.PENDING_PAYMENT) {
            throw new OrderDomainException("Order must be in CREATED or PENDING_PAYMENT state to be paid. Current: " + status);
        }
        if (paymentInfo == null) {
            throw new OrderDomainException("Cannot pay without payment info");
        }
        if (paymentInfo.getMethod() == PaymentMethod.CASH) {
            throw new OrderDomainException("Cash on delivery orders do not require pre-payment");
        }
        
        this.status = OrderStatus.PAID;
        this.paymentInfo = new PaymentInfo(paymentInfo.getMethod(), PaymentStatus.PAID, paymentInfo.getTransactionId());
    }

    public void confirm() {
        if (status != OrderStatus.CREATED && status != OrderStatus.PAID) {
            throw new OrderDomainException("Order must be in CREATED or PAID state to be confirmed. Current: " + status);
        }
        if (status == OrderStatus.CREATED) {
            if (paymentInfo == null || paymentInfo.getMethod() != PaymentMethod.CASH) {
                throw new OrderDomainException("Only CASH on delivery orders can be confirmed directly from CREATED state");
            }
        }
        if (items.isEmpty()) {
            throw new OrderDomainException("Cannot confirm an order with no items");
        }
        this.status = OrderStatus.CONFIRMED;
    }

    public void startPreparing() {
        if (status != OrderStatus.CONFIRMED) {
            throw new OrderDomainException("Order must be CONFIRMED to start preparing. Current: " + status);
        }
        this.status = OrderStatus.PREPARING;
    }

    public void startDelivery() {
        if (status != OrderStatus.PREPARING) {
            throw new OrderDomainException("Order must be in PREPARING state to start delivery. Current: " + status);
        }
        this.status = OrderStatus.OUT_FOR_DELIVERY;
    }

    public void deliver() {
        if (status != OrderStatus.OUT_FOR_DELIVERY) {
            throw new OrderDomainException("Order must be in OUT_FOR_DELIVERY state to be delivered. Current: " + status);
        }
        this.status = OrderStatus.DELIVERED;
        
        if (paymentInfo != null && paymentInfo.getMethod() == PaymentMethod.CASH) {
            this.paymentInfo = new PaymentInfo(PaymentMethod.CASH, PaymentStatus.PAID, "COD-" + id);
        }
    }

    public void cancel(String reason) {
        if (status == OrderStatus.PREPARING || status == OrderStatus.OUT_FOR_DELIVERY || status == OrderStatus.DELIVERED) {
            throw new OrderDomainException("Cannot cancel order in state: " + status);
        }
        if (status == OrderStatus.CANCELLED) {
            return;
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new OrderDomainException("Cancellation reason must be provided");
        }
        this.status = OrderStatus.CANCELLED;
    }

    private void validateItemsModifiable() {
        if (status == OrderStatus.CONFIRMED || status == OrderStatus.PREPARING || 
            status == OrderStatus.OUT_FOR_DELIVERY || status == OrderStatus.DELIVERED || 
            status == OrderStatus.CANCELLED) {
            throw new OrderDomainException("Cannot modify order items when order is " + status);
        }
    }

    private void recalculateTotalAmount() {
        String currency = items.isEmpty() ? "VND" : items.get(0).getPrice().getCurrency();
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : items) {
            total = total.add(item.getTotalPrice().getAmount());
        }
        this.totalAmount = Money.of(total, currency);
    }
}
