package mss301.se1911.group.assignment.paymentservices.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentTransaction {
    private UUID id;
    private UUID orderId;
    private BigDecimal amount;
    private String currency;
    private String status; // PENDING, SUCCESS, FAILED, REFUNDED

    public PaymentTransaction() {}

    public PaymentTransaction(UUID id, UUID orderId, BigDecimal amount, String currency, String status) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
