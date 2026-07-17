package mss301.se1911.group.assignment.customerservice.infrastructure.persistence;

import mss301.se1911.group.assignment.customerservice.domain.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaCustomerRepository extends JpaRepository<CustomerEntity, UUID> {
}
