package mss301.se1911.group.assignment.paymentservices.application.command;

import mss301.se1911.group.assignment.paymentservices.domain.vo.Owner;
import mss301.se1911.group.assignment.paymentservices.domain.vo.OwnerType;

import java.util.UUID;

public record CreateWalletCommand(UUID ownerId, OwnerType ownerType) {

    /**
     * Creates an Owner VO from this command's fields.
     */
    public Owner toOwner() {
        return Owner.of(ownerId, ownerType);
    }
}
