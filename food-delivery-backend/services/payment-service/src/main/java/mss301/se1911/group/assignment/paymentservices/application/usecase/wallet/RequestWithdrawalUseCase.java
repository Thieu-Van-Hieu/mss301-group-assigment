package mss301.se1911.group.assignment.paymentservices.application.usecase.wallet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.paymentservices.domain.aggregate.WalletAggregate;
import mss301.se1911.group.assignment.paymentservices.domain.entity.Wallet;
import mss301.se1911.group.assignment.paymentservices.domain.entity.WithdrawalRequest;
import mss301.se1911.group.assignment.paymentservices.domain.repository.WalletRepository;
import mss301.se1911.group.assignment.paymentservices.domain.repository.WithdrawalRequestRepository;
import mss301.se1911.group.assignment.paymentservices.domain.vo.OwnerType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestWithdrawalUseCase {

    private final GetWalletByOwnerUseCase getWalletByOwnerUseCase;
    private final WalletRepository walletRepository;
    private final WithdrawalRequestRepository withdrawalRequestRepository;

    @Transactional
    public WithdrawalRequest execute(UUID ownerId, OwnerType ownerType, BigDecimal amount, String bankName, String accountNumber) {
        Wallet wallet = getWalletByOwnerUseCase.execute(ownerId, ownerType);
        
        // Lock wallet to prevent race condition on balance
        wallet = walletRepository.findByIdForUpdate(wallet.getId()).orElseThrow();
        WalletAggregate aggregate = WalletAggregate.from(wallet);

        // This will throw InsufficientBalanceException if amount > balance
        aggregate.debit(amount, null, "Withdrawal to " + bankName + " (" + accountNumber + ")");
        
        WithdrawalRequest request = WithdrawalRequest.builder()
                .walletId(wallet.getId())
                .amount(amount)
                .bankName(bankName)
                .accountNumber(accountNumber)
                .status(WithdrawalRequest.WithdrawalStatus.PENDING)
                .build();

        withdrawalRequestRepository.save(request);
        walletRepository.save(aggregate.getWallet());

        log.info("Created withdrawal request {} for wallet {}", request.getId(), wallet.getId());
        return request;
    }
}
