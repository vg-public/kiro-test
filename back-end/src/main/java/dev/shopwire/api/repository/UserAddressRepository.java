package dev.shopwire.api.repository;

import dev.shopwire.api.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserAddressRepository extends JpaRepository<UserAddress, UUID> {
    List<UserAddress> findByUserUserId(UUID userId);
    Optional<UserAddress> findByAddressIdAndUserUserId(UUID addressId, UUID userId);
}
