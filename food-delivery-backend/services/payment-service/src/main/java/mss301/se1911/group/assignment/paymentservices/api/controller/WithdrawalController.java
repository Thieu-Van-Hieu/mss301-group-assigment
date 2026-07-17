package mss301.se1911.group.assignment.paymentservices.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.commonsecurity.filter.UserPrincipal;
import mss301.se1911.group.assignment.paymentservices.api.dto.request.WithdrawalRequestDto;
import mss301.se1911.group.assignment.paymentservices.api.dto.response.ApiResponse;
import mss301.se1911.group.assignment.paymentservices.application.usecase.wallet.RequestWithdrawalUseCase;
import mss301.se1911.group.assignment.paymentservices.domain.entity.WithdrawalRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets/withdraw")
@RequiredArgsConstructor
public class WithdrawalController {

    private final RequestWithdrawalUseCase requestWithdrawalUseCase;

    @PostMapping
    @PreAuthorize("hasAnyRole('RESTAURANT', 'DRIVER', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<WithdrawalRequest>> requestWithdrawal(
            @Valid @RequestBody WithdrawalRequestDto requestDto,
            Authentication authentication) {
        
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        UUID ownerId = UUID.fromString(principal.id());

        WithdrawalRequest request = requestWithdrawalUseCase.execute(
                ownerId,
                requestDto.ownerType(),
                requestDto.amount(),
                requestDto.bankName(),
                requestDto.accountNumber()
        );

        return ResponseEntity.ok(ApiResponse.success("Withdrawal request created successfully", request));
    }
}
