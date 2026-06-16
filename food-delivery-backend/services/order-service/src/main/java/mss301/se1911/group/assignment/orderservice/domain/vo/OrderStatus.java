package mss301.se1911.group.assignment.orderservice.domain.vo;

public enum OrderStatus {
    CREATED,
    PENDING_PAYMENT,
    PAID,
    CONFIRMED,
    PREPARING,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
}
