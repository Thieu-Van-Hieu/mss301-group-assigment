package mss301.se1911.group.assignment.customerservice.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.customerservice.domain.aggregate.AddressAggregate;
import mss301.se1911.group.assignment.customerservice.domain.repository.AddressRepository;
import mss301.se1911.group.assignment.customerservice.infrastructure.persistence.JpaAddressRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AddressRepositoryAdapter implements AddressRepository {

    private final JpaAddressRepository jpaAddressRepository;

    @Override
    public void save(AddressAggregate addressAggregate) {
        jpaAddressRepository.save(addressAggregate.getRootEntity());
    }

    @Override
    public Optional<AddressAggregate> findById(UUID id) {
        return jpaAddressRepository.findById(id).map(AddressAggregate::new);
    }

    @Override
    public List<AddressAggregate> findByCustomerId(UUID customerId) {
        return jpaAddressRepository.findByCustomerId(customerId).stream()
                .map(AddressAggregate::new)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaAddressRepository.deleteById(id);
    }

    @Override
    public void clearDefaultForCustomer(UUID customerId) {
        jpaAddressRepository.clearDefaultForCustomer(customerId);
    }
}
