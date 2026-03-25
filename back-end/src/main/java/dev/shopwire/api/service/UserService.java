package dev.shopwire.api.service;

import dev.shopwire.api.dto.user.AddressDto;
import dev.shopwire.api.dto.user.AddressRequest;
import dev.shopwire.api.dto.user.UpdateProfileRequest;
import dev.shopwire.api.dto.user.UserProfileDto;
import dev.shopwire.api.entity.User;
import dev.shopwire.api.entity.UserAddress;
import dev.shopwire.api.exception.ApiException;
import dev.shopwire.api.repository.UserAddressRepository;
import dev.shopwire.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserAddressRepository addressRepository;
    private final DtoMapper mapper;

    public UserProfileDto getProfile(UUID userId) {
        return mapper.toUserProfile(findUser(userId));
    }

    @Transactional
    public UserProfileDto updateProfile(UUID userId, UpdateProfileRequest req) {
        User user = findUser(userId);
        if (req.first_name() != null) user.setFirstName(req.first_name());
        if (req.last_name() != null) user.setLastName(req.last_name());
        if (req.phone() != null) user.setPhone(req.phone());
        return mapper.toUserProfile(userRepository.save(user));
    }

    public List<AddressDto> getAddresses(UUID userId) {
        return addressRepository.findByUserUserId(userId).stream()
                .map(mapper::toAddressDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressDto addAddress(UUID userId, AddressRequest req) {
        User user = findUser(userId);
        UserAddress address = UserAddress.builder()
                .user(user)
                .label(req.label())
                .fullName(req.full_name())
                .line1(req.line1())
                .line2(req.line2())
                .city(req.city())
                .state(req.state())
                .postalCode(req.postal_code())
                .country(req.country() != null ? req.country() : "US")
                .isDefault(req.is_default() != null && req.is_default())
                .build();
        if (address.isDefault()) {
            clearDefaultAddresses(userId);
        }
        return mapper.toAddressDto(addressRepository.save(address));
    }

    @Transactional
    public AddressDto updateAddress(UUID userId, UUID addressId, AddressRequest req) {
        UserAddress address = addressRepository.findByAddressIdAndUserUserId(addressId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Address not found"));
        address.setLabel(req.label());
        address.setFullName(req.full_name());
        address.setLine1(req.line1());
        address.setLine2(req.line2());
        address.setCity(req.city());
        address.setState(req.state());
        address.setPostalCode(req.postal_code());
        if (req.country() != null) address.setCountry(req.country());
        if (req.is_default() != null) {
            if (req.is_default()) clearDefaultAddresses(userId);
            address.setDefault(req.is_default());
        }
        return mapper.toAddressDto(addressRepository.save(address));
    }

    @Transactional
    public void deleteAddress(UUID userId, UUID addressId) {
        UserAddress address = addressRepository.findByAddressIdAndUserUserId(addressId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Address not found"));
        addressRepository.delete(address);
    }

    private void clearDefaultAddresses(UUID userId) {
        addressRepository.findByUserUserId(userId).forEach(a -> {
            if (a.isDefault()) {
                a.setDefault(false);
                addressRepository.save(a);
            }
        });
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "User not found"));
    }
}
