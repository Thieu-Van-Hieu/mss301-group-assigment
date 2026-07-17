package mss301.se1911.group.assignment.paymentservices.application.usecase.payout;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.paymentservices.application.usecase.wallet.GetWalletByOwnerUseCase;
import mss301.se1911.group.assignment.paymentservices.domain.aggregate.WalletAggregate;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PayoutRecord;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PayoutRecord.SettlementStatus;
import mss301.se1911.group.assignment.paymentservices.domain.entity.Wallet;
import mss301.se1911.group.assignment.paymentservices.domain.repository.PayoutRecordRepository;
import mss301.se1911.group.assignment.paymentservices.domain.repository.WalletRepository;
import mss301.se1911.group.assignment.paymentservices.domain.vo.OwnerType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlePendingPayoutsUseCase {

    private final PayoutRecordRepository payoutRecordRepository;
    private final WalletRepository walletRepository;
    private final GetWalletByOwnerUseCase getWalletByOwnerUseCase;

    // Run daily at 1:00 AM
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void execute() {
        log.info("Starting daily settlement of pending payouts...");
        OffsetDateTime now = OffsetDateTime.now();

        // 1. Settle Driver payouts (hold for 1 day)
        OffsetDateTime driverThreshold = now.minusDays(1);
        List<PayoutRecord> pendingDriverRecords = payoutRecordRepository.findByStatusAndDriverSettlementStatusAndCreatedAtBefore(
                PayoutRecord.PayoutStatus.COMPLETED, SettlementStatus.PENDING, driverThreshold);

        for (PayoutRecord record : pendingDriverRecords) {
            try {
                Wallet driverWallet = getWalletByOwnerUseCase.execute(record.getDriverId(), OwnerType.DRIVER);
                // lock wallet
                driverWallet = walletRepository.findByIdForUpdate(driverWallet.getId()).orElseThrow();
                WalletAggregate aggregate = WalletAggregate.from(driverWallet);
                
                aggregate.settlePending(record.getBreakdown().getDriverPayout(), record.getId(), "Settled driver payout for order " + record.getOrderId());
                walletRepository.save(aggregate.getWallet());

                record.setDriverSettlementStatus(SettlementStatus.SETTLED);
                record.setDriverSettledAt(now);
                payoutRecordRepository.save(record);
                log.info("Settled driver payout {} for order {}", record.getBreakdown().getDriverPayout(), record.getOrderId());
            } catch (Exception e) {
                log.error("Failed to settle driver payout for record {}", record.getId(), e);
            }
        }

        // 2. Settle Restaurant payouts (hold for 3 days)
        OffsetDateTime restaurantThreshold = now.minusDays(3);
        List<PayoutRecord> pendingRestaurantRecords = payoutRecordRepository.findByStatusAndRestaurantSettlementStatusAndCreatedAtBefore(
                PayoutRecord.PayoutStatus.COMPLETED, SettlementStatus.PENDING, restaurantThreshold);

        for (PayoutRecord record : pendingRestaurantRecords) {
            try {
                Wallet restaurantWallet = getWalletByOwnerUseCase.execute(record.getRestaurantId(), OwnerType.RESTAURANT);
                // lock wallet
                restaurantWallet = walletRepository.findByIdForUpdate(restaurantWallet.getId()).orElseThrow();
                WalletAggregate aggregate = WalletAggregate.from(restaurantWallet);
                
                aggregate.settlePending(record.getBreakdown().getRestaurantPayout(), record.getId(), "Settled restaurant payout for order " + record.getOrderId());
                walletRepository.save(aggregate.getWallet());

                record.setRestaurantSettlementStatus(SettlementStatus.SETTLED);
                record.setRestaurantSettledAt(now);
                payoutRecordRepository.save(record);
                log.info("Settled restaurant payout {} for order {}", record.getBreakdown().getRestaurantPayout(), record.getOrderId());
            } catch (Exception e) {
                log.error("Failed to settle restaurant payout for record {}", record.getId(), e);
            }
        }

        log.info("Finished daily settlement of pending payouts.");
    }
}
