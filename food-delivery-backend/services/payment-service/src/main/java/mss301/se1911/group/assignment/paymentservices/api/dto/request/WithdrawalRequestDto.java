package mss301.se1911.group.assignment.paymentservices.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;
import mss301.se1911.group.assignment.paymentservices.domain.vo.OwnerType;

public record WithdrawalRequestDto(
        @NotNull(message = "Owner type is required") OwnerType ownerType,
        @NotNull(message = "Amount is required") @Positive(message = "Amount must be positive") BigDecimal amount,
        @NotBlank(message = "Bank name is required") String bankName,
        @NotBlank(message = "Account number is required") String accountNumber
) {
}
