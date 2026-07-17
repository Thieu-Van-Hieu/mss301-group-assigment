package mss301.se1911.group.assignment.paymentservices.infrastructure.persistence;

import mss301.se1911.group.assignment.paymentservices.domain.entity.WalletLedger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for WalletLedger.
 */
@Repository
public interface WalletLedgerJpaRepository
        extends JpaRepository<WalletLedger, UUID>,
                JpaSpecificationExecutor<WalletLedger> {

    Page<WalletLedger> findByWalletIdOrderByCreatedAtDesc(UUID walletId, Pageable pageable);

    List<WalletLedger> findByTransactionRefId(UUID transactionRefId);

    boolean existsByWalletIdAndTransactionRefId(UUID walletId, UUID transactionRefId);
}
