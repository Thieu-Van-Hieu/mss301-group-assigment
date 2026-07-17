package mss301.se1911.group.assignment.customerservice.domain.repository;

import mss301.se1911.group.assignment.customerservice.domain.aggregate.CustomerAggregate;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

    void save(CustomerAggregate customerAggregate);

    Optional<CustomerAggregate> findById(UUID id);

    boolean existsById(UUID id);
}
