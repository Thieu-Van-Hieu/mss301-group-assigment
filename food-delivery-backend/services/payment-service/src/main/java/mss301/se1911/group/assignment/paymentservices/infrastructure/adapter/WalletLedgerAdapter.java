package mss301.se1911.group.assignment.paymentservices.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.paymentservices.domain.entity.WalletLedger;
import mss301.se1911.group.assignment.paymentservices.domain.repository.WalletLedgerRepository;
import mss301.se1911.group.assignment.paymentservices.infrastructure.persistence.WalletLedgerJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Adapter that implements the domain WalletLedgerRepository port.
 */
@Repository
@RequiredArgsConstructor
public class WalletLedgerAdapter implements WalletLedgerRepository {

    private final WalletLedgerJpaRepository jpaRepository;

    @Override
    public WalletLedger save(WalletLedger ledger) {
        return jpaRepository.save(ledger);
    }

    @Override
    public Page<WalletLedger> findByWalletIdOrderByCreatedAtDesc(UUID walletId, Pageable pageable) {
        return jpaRepository.findByWalletIdOrderByCreatedAtDesc(walletId, pageable);
    }

    @Override
    public List<WalletLedger> findByTransactionRefId(UUID transactionRefId) {
        return jpaRepository.findByTransactionRefId(transactionRefId);
    }

    @Override
    public boolean existsByWalletIdAndTransactionRefId(UUID walletId, UUID transactionRefId) {
        return jpaRepository.existsByWalletIdAndTransactionRefId(walletId, transactionRefId);
    }
}
