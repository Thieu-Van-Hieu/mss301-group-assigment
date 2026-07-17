package mss301.se1911.group.assignment.customerservice.infrastructure.persistence;

import mss301.se1911.group.assignment.customerservice.domain.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface JpaAddressRepository extends JpaRepository<AddressEntity, UUID> {

    List<AddressEntity> findByCustomerId(UUID customerId);

    @Modifying
    @Query("UPDATE AddressEntity a SET a.isDefault = false WHERE a.customerId = :customerId")
    void clearDefaultForCustomer(@Param("customerId") UUID customerId);
}
