package mss301.se1911.group.assignment.orderservice.domain.aggregate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import mss301.se1911.group.assignment.orderservice.domain.exception.OrderDomainException;
import mss301.se1911.group.assignment.orderservice.domain.vo.Money;

import java.util.UUID;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class OrderItem {
    @EqualsAndHashCode.Include
    private final UUID productId;
    private final String name;
    private int quantity;
    private final Money price;

    public OrderItem(UUID productId, String name, int quantity, Money price) {
        if (productId == null) {
            throw new OrderDomainException("Product ID cannot be null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new OrderDomainException("Product name cannot be empty");
        }
        if (price == null) {
            throw new OrderDomainException("Product price cannot be null");
        }
        this.productId = productId;
        this.name = name;
        this.price = price;
        setQuantity(quantity);
    }

    public void updateQuantity(int newQuantity) {
        setQuantity(newQuantity);
    }

    public Money getTotalPrice() {
        return price.multiply(quantity);
    }

    private void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new OrderDomainException("Quantity must be greater than zero");
        }
        this.quantity = quantity;
    }
}
