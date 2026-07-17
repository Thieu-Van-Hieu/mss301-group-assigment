package mss301.se1911.group.assignment.paymentservices.domain.repository;

import mss301.se1911.group.assignment.paymentservices.domain.entity.Wallet;
import mss301.se1911.group.assignment.paymentservices.domain.vo.OwnerType;

import java.util.Optional;
import java.util.UUID;

/**
 * Domain port for Wallet persistence.
 * Locking semantics (e.g. SELECT FOR UPDATE) are an infrastructure concern
 * handled by the adapter implementation.
 */
public interface WalletRepository {

    Wallet save(Wallet wallet);

    Optional<Wallet> findById(UUID id);

    Optional<Wallet> findByOwnerIdAndOwnerType(UUID ownerId, OwnerType ownerType);

    boolean existsByOwnerIdAndOwnerType(UUID ownerId, OwnerType ownerType);

    /**
     * Finds a wallet by ID with a pessimistic write lock.
     * The locking mechanism is an adapter-layer concern.
     */
    Optional<Wallet> findByIdForUpdate(UUID id);

    /**
     * Finds a wallet by owner identity with a pessimistic write lock.
     * The locking mechanism is an adapter-layer concern.
     */
    Optional<Wallet> findByOwnerIdAndOwnerTypeForUpdate(UUID ownerId, OwnerType ownerType);
}
