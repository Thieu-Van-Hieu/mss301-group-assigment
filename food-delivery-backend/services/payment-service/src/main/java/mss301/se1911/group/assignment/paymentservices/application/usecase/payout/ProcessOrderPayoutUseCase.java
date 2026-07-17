package mss301.se1911.group.assignment.paymentservices.application.usecase.payout;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.paymentservices.application.command.CreateWalletCommand;
import mss301.se1911.group.assignment.paymentservices.application.command.ProcessPayoutCommand;
import mss301.se1911.group.assignment.paymentservices.application.usecase.payment.GetPaymentByOrderIdUseCase;
import mss301.se1911.group.assignment.paymentservices.application.usecase.wallet.CreateWalletUseCase;
import mss301.se1911.group.assignment.paymentservices.application.usecase.wallet.CreditPendingWalletUseCase;
import mss301.se1911.group.assignment.paymentservices.application.usecase.wallet.CreditWalletUseCase;
import mss301.se1911.group.assignment.paymentservices.application.usecase.wallet.ForceDebitWalletUseCase;
import mss301.se1911.group.assignment.paymentservices.application.usecase.wallet.GetWalletByOwnerUseCase;
import mss301.se1911.group.assignment.paymentservices.config.PlatformConfig;
import mss301.se1911.group.assignment.paymentservices.domain.aggregate.PayoutRecordAggregate;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction.PaymentMethod;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PayoutRecord;
import mss301.se1911.group.assignment.paymentservices.domain.entity.Wallet;
import mss301.se1911.group.assignment.paymentservices.domain.exception.DuplicatePayoutException;
import mss301.se1911.group.assignment.paymentservices.domain.exception.WalletNotFoundException;
import mss301.se1911.group.assignment.paymentservices.domain.repository.PayoutRecordRepository;
import mss301.se1911.group.assignment.paymentservices.domain.repository.WalletRepository;
import mss301.se1911.group.assignment.paymentservices.domain.vo.OwnerType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessOrderPayoutUseCase {

    private final GetPaymentByOrderIdUseCase getPaymentByOrderIdUseCase;
    private final GetWalletByOwnerUseCase getWalletByOwnerUseCase;
    private final CreateWalletUseCase createWalletUseCase;
    private final CreditWalletUseCase creditWalletUseCase;
    private final CreditPendingWalletUseCase creditPendingWalletUseCase;
    private final ForceDebitWalletUseCase forceDebitWalletUseCase;
    private final PayoutRecordRepository payoutRecordRepository;
    private final WalletRepository walletRepository;
    private final PlatformConfig platformConfig;

    // Hardcoded System ID for platform wallet since it's not in PlatformConfig
    private static final UUID SYSTEM_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Transactional
    public void execute(ProcessPayoutCommand command) {
        UUID orderId = command.event().orderId();
        if (payoutRecordRepository.existsByOrderId(orderId)) {
            throw new DuplicatePayoutException(orderId);
        }

        PaymentTransaction payment = getPaymentByOrderIdUseCase.execute(orderId);

        // Lock wallets
        Wallet restaurantWallet = getOrCreateWallet(command.event().restaurantId(), OwnerType.RESTAURANT);
        Wallet driverWallet = getOrCreateWallet(command.event().driverId(), OwnerType.DRIVER);
        Wallet systemWallet = getOrCreateWallet(SYSTEM_ID, OwnerType.PLATFORM);

        List<UUID> walletIdsToLock = new ArrayList<>();
        walletIdsToLock.add(restaurantWallet.getId());
        walletIdsToLock.add(driverWallet.getId());
        walletIdsToLock.add(systemWallet.getId());
        Collections.sort(walletIdsToLock); // Sort to prevent deadlocks
        
        // Lock individually since findAllByIdForUpdate is missing in repository interface
        for (UUID id : walletIdsToLock) {
            walletRepository.findByIdForUpdate(id);
        }

        PayoutRecordAggregate aggregate = PayoutRecordAggregate.calculate(
                orderId,
                payment.getId(),
                command.event().restaurantId(),
                command.event().driverId(),
                command.event().totalAmount(),
                command.event().deliveryFee(),
                platformConfig.getRestaurantCommissionRate(),
                platformConfig.getDriverCommissionRate()
        );

        aggregate.markCompleted(); // Mark completed before saving
        PayoutRecord record = aggregate.getRecord();
        payoutRecordRepository.save(record);

        if (payment.getPaymentMethod() == PaymentMethod.COD) {
            BigDecimal driverDebt = aggregate.getRestaurantPayout().add(aggregate.getPlatformFee());
            forceDebitWalletUseCase.execute(
                    driverWallet.getId(),
                    driverDebt,
                    record.getId(),
                    "COD debt collection for order " + orderId
            );

            creditPendingWalletUseCase.execute(
                    restaurantWallet.getId(),
                    aggregate.getRestaurantPayout()
            );

            creditWalletUseCase.execute(
                    systemWallet.getId(),
                    aggregate.getPlatformFee(),
                    record.getId(),
                    "Platform fee for COD order " + orderId
            );
        } else {
            creditPendingWalletUseCase.execute(
                    restaurantWallet.getId(),
                    aggregate.getRestaurantPayout()
            );

            creditPendingWalletUseCase.execute(
                    driverWallet.getId(),
                    aggregate.getDriverPayout()
            );

            creditWalletUseCase.execute(
                    systemWallet.getId(),
                    aggregate.getPlatformFee(),
                    record.getId(),
                    "Platform fee for order " + orderId
            );
        }

        log.info("Processed payout for order {} successfully", orderId);
    }

    private Wallet getOrCreateWallet(UUID ownerId, OwnerType type) {
        try {
            return getWalletByOwnerUseCase.execute(ownerId, type);
        } catch (WalletNotFoundException e) {
            log.info("Wallet not found for {} (Type: {}). Creating one...", ownerId, type);
            return createWalletUseCase.execute(new CreateWalletCommand(ownerId, type));
        }
    }
}
