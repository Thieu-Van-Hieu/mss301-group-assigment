package mss301.se1911.group.assignment.paymentservices.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import mss301.se1911.group.assignment.paymentservices.domain.entity.Wallet;
import mss301.se1911.group.assignment.paymentservices.domain.vo.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for Wallet.
 * Contains pessimistic locking queries — this is an infrastructure concern.
 * <p>
 * Note: Owner fields are accessed via embedded path (owner.ownerId, owner.ownerType).
 */
@Repository
public interface WalletJpaRepository
        extends JpaRepository<Wallet, UUID>,
                JpaSpecificationExecutor<Wallet> {

    @Query("SELECT w FROM Wallet w WHERE w.owner.ownerId = :ownerId AND w.owner.ownerType = :ownerType")
    Optional<Wallet> findByOwnerIdAndOwnerType(@Param("ownerId") UUID ownerId, @Param("ownerType") OwnerType ownerType);

    @Query("SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END FROM Wallet w WHERE w.owner.ownerId = :ownerId AND w.owner.ownerType = :ownerType")
    boolean existsByOwnerIdAndOwnerType(@Param("ownerId") UUID ownerId, @Param("ownerType") OwnerType ownerType);

    /**
     * Pessimistic write lock (SELECT ... FOR UPDATE) to prevent race conditions
     * when modifying wallet balance. Locks the row until the transaction commits.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.id = :id")
    Optional<Wallet> findByIdForUpdate(@Param("id") UUID id);

    /**
     * Pessimistic write lock by owner identity.
     * Used during payout when we know owner but not wallet ID.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.owner.ownerId = :ownerId AND w.owner.ownerType = :ownerType")
    Optional<Wallet> findByOwnerIdAndOwnerTypeForUpdate(
            @Param("ownerId") UUID ownerId,
            @Param("ownerType") OwnerType ownerType
    );
}
