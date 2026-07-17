package mss301.se1911.group.assignment.paymentservices.domain.repository;

import mss301.se1911.group.assignment.paymentservices.domain.entity.WithdrawalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, UUID> {
}
