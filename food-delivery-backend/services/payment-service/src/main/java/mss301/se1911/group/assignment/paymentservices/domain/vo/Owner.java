package mss301.se1911.group.assignment.paymentservices.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing the owner of a wallet.
 * <p>
 * Immutable — once created, cannot be modified.
 * Equality is determined by both {@code ownerId} and {@code ownerType}.
 * <p>
 * Business rules:
 * <ul>
 *   <li>ownerId must not be null</li>
 *   <li>ownerType must not be null</li>
 * </ul>
 */
@Embeddable
public final class Owner {

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type", nullable = false, length = 20)
    private OwnerType ownerType;

    /** JPA requires a no-arg constructor. */
    protected Owner() {
    }

    private Owner(UUID ownerId, OwnerType ownerType) {
        Objects.requireNonNull(ownerId, "ownerId must not be null");
        Objects.requireNonNull(ownerType, "ownerType must not be null");
        this.ownerId = ownerId;
        this.ownerType = ownerType;
    }

    // ── Factory Method ──

    public static Owner of(UUID ownerId, OwnerType ownerType) {
        return new Owner(ownerId, ownerType);
    }

    // ── Getters ──

    public UUID getOwnerId() {
        return ownerId;
    }

    public OwnerType getOwnerType() {
        return ownerType;
    }

    // ── Query Methods ──

    public boolean isCustomer() {
        return ownerType == OwnerType.CUSTOMER;
    }

    public boolean isRestaurant() {
        return ownerType == OwnerType.RESTAURANT;
    }

    public boolean isDriver() {
        return ownerType == OwnerType.DRIVER;
    }

    public boolean isPlatform() {
        return ownerType == OwnerType.PLATFORM;
    }

    // ── Equality & Hash (Value Object semantics) ──

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Owner other)) return false;
        return ownerId.equals(other.ownerId) && ownerType == other.ownerType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerId, ownerType);
    }

    @Override
    public String toString() {
        return ownerType + ":" + ownerId;
    }
}
