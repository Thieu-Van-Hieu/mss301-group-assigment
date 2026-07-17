package mss301.se1911.group.assignment.paymentservices.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.paymentservices.domain.entity.Wallet;
import mss301.se1911.group.assignment.paymentservices.domain.repository.WalletRepository;
import mss301.se1911.group.assignment.paymentservices.domain.vo.OwnerType;
import mss301.se1911.group.assignment.paymentservices.infrastructure.persistence.WalletJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter that implements the domain WalletRepository port.
 * Pessimistic locking is handled here via the JPA repository's
 * {@code @Lock} annotated methods — keeping it an infrastructure concern.
 */
@Repository
@RequiredArgsConstructor
public class WalletAdapter implements WalletRepository {

    private final WalletJpaRepository jpaRepository;

    @Override
    public Wallet save(Wallet wallet) {
        return jpaRepository.save(wallet);
    }

    @Override
    public Optional<Wallet> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Wallet> findByOwnerIdAndOwnerType(UUID ownerId, OwnerType ownerType) {
        return jpaRepository.findByOwnerIdAndOwnerType(ownerId, ownerType);
    }

    @Override
    public boolean existsByOwnerIdAndOwnerType(UUID ownerId, OwnerType ownerType) {
        return jpaRepository.existsByOwnerIdAndOwnerType(ownerId, ownerType);
    }

    @Override
    public Optional<Wallet> findByIdForUpdate(UUID id) {
        return jpaRepository.findByIdForUpdate(id);
    }

    @Override
    public Optional<Wallet> findByOwnerIdAndOwnerTypeForUpdate(UUID ownerId, OwnerType ownerType) {
        return jpaRepository.findByOwnerIdAndOwnerTypeForUpdate(ownerId, ownerType);
    }
}
