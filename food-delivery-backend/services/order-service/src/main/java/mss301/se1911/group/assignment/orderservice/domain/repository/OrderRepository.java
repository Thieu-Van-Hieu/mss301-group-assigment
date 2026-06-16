package mss301.se1911.group.assignment.orderservice.domain.repository;

import mss301.se1911.group.assignment.orderservice.domain.aggregate.OrderAggregate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    OrderAggregate save(OrderAggregate order);
    Optional<OrderAggregate> findById(UUID id);
    List<OrderAggregate> findAll();
}
