package mss301.se1911.group.assignment.paymentservices.domain.exception;

import java.util.UUID;

public class WalletNotFoundException extends RuntimeException {

    public WalletNotFoundException(UUID walletId) {
        super("Wallet not found with id: " + walletId);
    }

    public WalletNotFoundException(UUID ownerId, String ownerType) {
        super("Wallet not found for owner: " + ownerId + " (Type: " + ownerType + ")");
    }
}
