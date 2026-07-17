package mss301.se1911.group.assignment.customerservice.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.customerservice.api.dto.request.UpdateCustomerRequest;
import mss301.se1911.group.assignment.customerservice.application.dto.CustomerResponse;
import mss301.se1911.group.assignment.customerservice.application.usecase.CustomerQueryUseCase;
import mss301.se1911.group.assignment.customerservice.application.usecase.UpdateCustomerUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerQueryUseCase customerQueryUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable UUID id) {
        return ResponseEntity.ok(customerQueryUseCase.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomerRequest request) {
        return ResponseEntity.ok(updateCustomerUseCase.execute(request.toCommand(id)));
    }
}
