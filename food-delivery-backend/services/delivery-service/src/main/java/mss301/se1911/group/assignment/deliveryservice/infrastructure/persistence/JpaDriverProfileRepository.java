package mss301.se1911.group.assignment.deliveryservice.infrastructure.persistence;

import mss301.se1911.group.assignment.deliveryservice.domain.entity.DriverProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface JpaDriverProfileRepository extends JpaRepository<DriverProfileEntity, UUID> ,
                                                    JpaSpecificationExecutor<DriverProfileEntity> {
}
