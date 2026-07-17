package mss301.se1911.group.assignment.customerservice.domain.repository;

import mss301.se1911.group.assignment.customerservice.domain.aggregate.AddressAggregate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository {

    void save(AddressAggregate addressAggregate);

    Optional<AddressAggregate> findById(UUID id);

    List<AddressAggregate> findByCustomerId(UUID customerId);

    void deleteById(UUID id);

    /**
     * Bỏ cờ mặc định của tất cả địa chỉ thuộc một khách hàng (để đảm bảo chỉ có 1 địa chỉ mặc định).
     */
    void clearDefaultForCustomer(UUID customerId);
}
