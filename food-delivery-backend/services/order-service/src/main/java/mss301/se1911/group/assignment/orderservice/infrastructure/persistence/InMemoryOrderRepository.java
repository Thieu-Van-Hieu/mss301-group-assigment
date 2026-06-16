package mss301.se1911.group.assignment.orderservice.infrastructure.persistence;

import org.springframework.stereotype.Repository;
import mss301.se1911.group.assignment.orderservice.domain.aggregate.OrderAggregate;
import mss301.se1911.group.assignment.orderservice.domain.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<UUID, OrderAggregate> store = new ConcurrentHashMap<>();

    @Override
    public OrderAggregate save(OrderAggregate order) {
        if (order == null || order.getId() == null) {
            throw new IllegalArgumentException("Order and order ID must not be null");
        }
        store.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<OrderAggregate> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<OrderAggregate> findAll() {
        return new ArrayList<>(store.values());
    }
}
