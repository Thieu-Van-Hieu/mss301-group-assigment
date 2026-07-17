package mss301.se1911.group.assignment.paymentservices.domain.repository;

import mss301.se1911.group.assignment.paymentservices.domain.entity.WalletLedger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Domain port for WalletLedger persistence.
 * Infrastructure adapters provide the concrete implementation.
 */
public interface WalletLedgerRepository {

    WalletLedger save(WalletLedger ledger);

    Page<WalletLedger> findByWalletIdOrderByCreatedAtDesc(UUID walletId, Pageable pageable);

    List<WalletLedger> findByTransactionRefId(UUID transactionRefId);

    boolean existsByWalletIdAndTransactionRefId(UUID walletId, UUID transactionRefId);
}
