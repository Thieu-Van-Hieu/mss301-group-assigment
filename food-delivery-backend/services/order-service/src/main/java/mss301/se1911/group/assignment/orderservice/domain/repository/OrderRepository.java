package mss301.se1911.group.assignment.orderservice.domain.repository;

import mss301.se1911.group.assignment.orderservice.domain.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(UUID id);
    List<Order> findAll();
}
