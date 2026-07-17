package mss301.se1911.group.assignment.paymentservices.application.usecase.wallet;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.paymentservices.domain.entity.WalletLedger;
import mss301.se1911.group.assignment.paymentservices.domain.repository.WalletLedgerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetWalletLedgerUseCase {

    private final WalletLedgerRepository walletLedgerRepository;

    @Transactional(readOnly = true)
    public Page<WalletLedger> execute(UUID walletId, Pageable pageable) {
        return walletLedgerRepository.findByWalletIdOrderByCreatedAtDesc(walletId, pageable);
    }
}
