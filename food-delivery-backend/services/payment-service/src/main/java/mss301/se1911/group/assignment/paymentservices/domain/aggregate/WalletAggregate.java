package mss301.se1911.group.assignment.paymentservices.domain.aggregate;

import lombok.Getter;
import mss301.se1911.group.assignment.paymentservices.domain.entity.Wallet;
import mss301.se1911.group.assignment.paymentservices.domain.entity.WalletLedger;
import mss301.se1911.group.assignment.paymentservices.domain.exception.InsufficientBalanceException;
import mss301.se1911.group.assignment.paymentservices.domain.exception.PaymentProcessingException;
import mss301.se1911.group.assignment.paymentservices.domain.entity.Wallet.WalletStatus;
import mss301.se1911.group.assignment.paymentservices.domain.entity.WalletLedger.LedgerEntryType;
import mss301.se1911.group.assignment.paymentservices.domain.vo.Money;
import mss301.se1911.group.assignment.paymentservices.domain.vo.Owner;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate root for Wallet.
 * Encapsulates credit/debit business rules and produces WalletLedger entries
 * as part of each balance mutation.
 */
@Getter
public class WalletAggregate {

    private final Wallet wallet;

    private WalletAggregate(Wallet wallet) {
        this.wallet = wallet;
    }

    /**
     * Wraps an existing wallet entity loaded from persistence.
     */
    public static WalletAggregate from(Wallet wallet) {
        Objects.requireNonNull(wallet, "Wallet must not be null");
        return new WalletAggregate(wallet);
    }

    /**
     * Factory: create a brand-new wallet with zero balance and ACTIVE status.
     */
    public static WalletAggregate create(Owner owner) {
        Objects.requireNonNull(owner, "owner must not be null");

        Wallet wallet = Wallet.builder()
                .owner(owner)
                .balance(Money.zero())
                .pendingBalance(Money.zero())
                .status(WalletStatus.ACTIVE)
                .build();

        return new WalletAggregate(wallet);
    }

    // ── Balance Mutation Methods ──

    /**
     * Credits (adds) the given amount to the wallet balance.
     *
     * @return a new WalletLedger entry recording this credit
     * @throws PaymentProcessingException if wallet is inactive or amount is non-positive
     */
    public WalletLedger credit(BigDecimal amount, UUID transactionRefId, String description) {
        validateActive();
        validatePositiveAmount(amount, "Credit");

        BigDecimal balanceBefore = wallet.getBalance().getAmount();
        BigDecimal balanceAfter = balanceBefore.add(amount);

        wallet.setBalance(Money.of(balanceAfter, wallet.getBalance().getCurrency()));

        return buildLedgerEntry(transactionRefId, LedgerEntryType.CREDIT,
                amount, balanceBefore, balanceAfter, description);
    }

    /**
     * Debits (subtracts) the given amount from the wallet balance.
     *
     * @return a new WalletLedger entry recording this debit
     * @throws InsufficientBalanceException if balance is less than amount
     * @throws PaymentProcessingException   if wallet is inactive or amount is non-positive
     */
    public WalletLedger debit(BigDecimal amount, UUID transactionRefId, String description) {
        validateActive();
        validatePositiveAmount(amount, "Debit");

        BigDecimal balanceBefore = wallet.getBalance().getAmount();

        if (balanceBefore.compareTo(amount) < 0) {
            throw new InsufficientBalanceException(wallet.getId(), amount, balanceBefore);
        }

        BigDecimal balanceAfter = balanceBefore.subtract(amount);

        wallet.setBalance(Money.of(balanceAfter, wallet.getBalance().getCurrency()));

        return buildLedgerEntry(transactionRefId, LedgerEntryType.DEBIT,
                amount, balanceBefore, balanceAfter, description);
    }

    /**
     * Force debits the given amount from the wallet balance without checking if the balance is sufficient.
     * This is used for system operations like COD debt collection, allowing the balance to go negative.
     *
     * @return a new WalletLedger entry recording this debit
     * @throws PaymentProcessingException if wallet is inactive or amount is non-positive
     */
    public WalletLedger forceDebit(BigDecimal amount, UUID transactionRefId, String description) {
        validateActive();
        validatePositiveAmount(amount, "Force Debit");

        BigDecimal balanceBefore = wallet.getBalance().getAmount();
        BigDecimal balanceAfter = balanceBefore.subtract(amount);

        wallet.setBalance(Money.of(balanceAfter, wallet.getBalance().getCurrency()));

        return buildLedgerEntry(transactionRefId, LedgerEntryType.DEBIT,
                amount, balanceBefore, balanceAfter, description);
    }

    public void creditPending(BigDecimal amount) {
        validateActive();
        validatePositiveAmount(amount, "Credit Pending");

        BigDecimal pendingBefore = wallet.getPendingBalance().getAmount();
        BigDecimal pendingAfter = pendingBefore.add(amount);

        wallet.setPendingBalance(Money.of(pendingAfter, wallet.getPendingBalance().getCurrency()));
    }

    public WalletLedger settlePending(BigDecimal amount, UUID transactionRefId, String description) {
        validateActive();
        validatePositiveAmount(amount, "Settle Pending");

        BigDecimal pendingBefore = wallet.getPendingBalance().getAmount();
        if (pendingBefore.compareTo(amount) < 0) {
            throw new PaymentProcessingException("Insufficient pending balance to settle");
        }

        BigDecimal pendingAfter = pendingBefore.subtract(amount);
        wallet.setPendingBalance(Money.of(pendingAfter, wallet.getPendingBalance().getCurrency()));

        BigDecimal balanceBefore = wallet.getBalance().getAmount();
        BigDecimal balanceAfter = balanceBefore.add(amount);
        wallet.setBalance(Money.of(balanceAfter, wallet.getBalance().getCurrency()));

        return buildLedgerEntry(transactionRefId, LedgerEntryType.CREDIT,
                amount, balanceBefore, balanceAfter, description);
    }

    // ── Private Helpers ──

    private void validateActive() {
        if (wallet.getStatus() != WalletStatus.ACTIVE) {
            throw new PaymentProcessingException(
                    "Cannot operate on wallet in status: " + wallet.getStatus());
        }
    }

    private void validatePositiveAmount(BigDecimal amount, String operation) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentProcessingException(
                    operation + " amount must be greater than zero");
        }
    }

    private WalletLedger buildLedgerEntry(UUID transactionRefId, LedgerEntryType entryType,
                                          BigDecimal amount, BigDecimal balanceBefore,
                                          BigDecimal balanceAfter, String description) {
        return WalletLedger.builder()
                .walletId(wallet.getId())
                .transactionRefId(transactionRefId)
                .entryType(entryType)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .description(description)
                .build();
    }
}
