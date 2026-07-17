package mss301.se1911.group.assignment.paymentservices.application.usecase.wallet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.paymentservices.domain.aggregate.WalletAggregate;
import mss301.se1911.group.assignment.paymentservices.domain.entity.Wallet;
import mss301.se1911.group.assignment.paymentservices.domain.entity.WalletLedger;
import mss301.se1911.group.assignment.paymentservices.domain.exception.WalletNotFoundException;
import mss301.se1911.group.assignment.paymentservices.domain.repository.WalletLedgerRepository;
import mss301.se1911.group.assignment.paymentservices.domain.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditWalletUseCase {

    private final WalletRepository walletRepository;
    private final WalletLedgerRepository walletLedgerRepository;

    @Transactional
    public void execute(UUID walletId, BigDecimal amount, UUID transactionRefId, String description) {
        Wallet wallet = walletRepository.findByIdForUpdate(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));
        WalletAggregate aggregate = WalletAggregate.from(wallet);

        WalletLedger ledger = aggregate.credit(amount, transactionRefId, description);

        walletRepository.save(aggregate.getWallet());
        walletLedgerRepository.save(ledger);
        log.info("Credited {} to wallet {}. Balance: {} -> {}", amount, walletId,
                ledger.getBalanceBefore(), ledger.getBalanceAfter());
    }
}
