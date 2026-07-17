package mss301.se1911.group.assignment.customerservice.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.customerservice.api.dto.request.CreateAddressRequest;
import mss301.se1911.group.assignment.customerservice.api.dto.request.UpdateAddressRequest;
import mss301.se1911.group.assignment.customerservice.application.dto.AddressResponse;
import mss301.se1911.group.assignment.customerservice.application.usecase.AddressUseCases;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AddressController {

    private final AddressUseCases addressUseCases;

    @PostMapping("/customers/{customerId}/addresses")
    public ResponseEntity<AddressResponse> addAddress(
            @PathVariable UUID customerId,
            @Valid @RequestBody CreateAddressRequest request) {
        AddressResponse response = addressUseCases.add(request.toCommand(customerId));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/customers/{customerId}/addresses")
    public ResponseEntity<List<AddressResponse>> listAddresses(@PathVariable UUID customerId) {
        return ResponseEntity.ok(addressUseCases.listByCustomer(customerId));
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable UUID addressId,
            @Valid @RequestBody UpdateAddressRequest request) {
        return ResponseEntity.ok(addressUseCases.update(request.toCommand(addressId)));
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable UUID addressId) {
        addressUseCases.delete(addressId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/addresses/{addressId}/default")
    public ResponseEntity<AddressResponse> setDefaultAddress(@PathVariable UUID addressId) {
        return ResponseEntity.ok(addressUseCases.setDefault(addressId));
    }
}
