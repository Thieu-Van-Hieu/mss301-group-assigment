package mss301.se1911.group.assignment.customerservice.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.customerservice.domain.aggregate.CustomerAggregate;
import mss301.se1911.group.assignment.customerservice.domain.repository.CustomerRepository;
import mss301.se1911.group.assignment.customerservice.infrastructure.persistence.JpaCustomerRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final JpaCustomerRepository jpaCustomerRepository;

    @Override
    public void save(CustomerAggregate customerAggregate) {
        jpaCustomerRepository.save(customerAggregate.getRootEntity());
    }

    @Override
    public Optional<CustomerAggregate> findById(UUID id) {
        return jpaCustomerRepository.findById(id).map(CustomerAggregate::new);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaCustomerRepository.existsById(id);
    }
}
