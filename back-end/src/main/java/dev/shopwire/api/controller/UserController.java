package dev.shopwire.api.controller;

import dev.shopwire.api.dto.user.AddressDto;
import dev.shopwire.api.dto.user.AddressRequest;
import dev.shopwire.api.dto.user.UpdateProfileRequest;
import dev.shopwire.api.dto.user.UserProfileDto;
import dev.shopwire.api.security.SecurityUtils;
import dev.shopwire.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getProfile() {
        return ResponseEntity.ok(userService.getProfile(SecurityUtils.currentUserId()));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileDto> updateProfile(@RequestBody UpdateProfileRequest req) {
        return ResponseEntity.ok(userService.updateProfile(SecurityUtils.currentUserId(), req));
    }

    @GetMapping("/me/addresses")
    public ResponseEntity<List<AddressDto>> getAddresses() {
        return ResponseEntity.ok(userService.getAddresses(SecurityUtils.currentUserId()));
    }

    @PostMapping("/me/addresses")
    public ResponseEntity<AddressDto> addAddress(@Valid @RequestBody AddressRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.addAddress(SecurityUtils.currentUserId(), req));
    }

    @PutMapping("/me/addresses/{addressId}")
    public ResponseEntity<AddressDto> updateAddress(@PathVariable UUID addressId,
                                                     @Valid @RequestBody AddressRequest req) {
        return ResponseEntity.ok(userService.updateAddress(SecurityUtils.currentUserId(), addressId, req));
    }

    @DeleteMapping("/me/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable UUID addressId) {
        userService.deleteAddress(SecurityUtils.currentUserId(), addressId);
        return ResponseEntity.noContent().build();
    }
}
