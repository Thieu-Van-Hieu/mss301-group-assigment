package mss301.se1911.group.assignment.paymentservices.application.usecase.wallet;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.paymentservices.domain.entity.Wallet;
import mss301.se1911.group.assignment.paymentservices.domain.exception.WalletNotFoundException;
import mss301.se1911.group.assignment.paymentservices.domain.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetWalletByIdUseCase {

    private final WalletRepository walletRepository;

    @Transactional(readOnly = true)
    public Wallet execute(UUID walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));
    }
}
