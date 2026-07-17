package mss301.se1911.group.assignment.paymentservices.application.usecase.wallet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.paymentservices.application.command.CreateWalletCommand;
import mss301.se1911.group.assignment.paymentservices.domain.aggregate.WalletAggregate;
import mss301.se1911.group.assignment.paymentservices.domain.entity.Wallet;
import mss301.se1911.group.assignment.paymentservices.domain.exception.WalletAlreadyExistsException;
import mss301.se1911.group.assignment.paymentservices.domain.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateWalletUseCase {

    private final WalletRepository walletRepository;

    @Transactional
    public Wallet execute(CreateWalletCommand command) {
        if (walletRepository.existsByOwnerIdAndOwnerType(command.ownerId(), command.ownerType())) {
            throw new WalletAlreadyExistsException(command.ownerId(), command.ownerType());
        }

        WalletAggregate aggregate = WalletAggregate.create(command.toOwner());
        Wallet saved = walletRepository.save(aggregate.getWallet());
        
        log.info("Created wallet {} for owner {} of type {}", saved.getId(), command.ownerId(), command.ownerType());
        return saved;
    }
}
