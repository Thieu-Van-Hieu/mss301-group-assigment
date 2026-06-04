package mss301.se1911.group.assignment.paymentservices.domain.exception;

import mss301.se1911.group.assignment.paymentservices.domain.vo.OwnerType;
import java.util.UUID;

public class WalletAlreadyExistsException extends RuntimeException {
    public WalletAlreadyExistsException(UUID ownerId, OwnerType ownerType) {
        super(String.format("Wallet already exists for owner %s of type %s", ownerId, ownerType));
    }
}
