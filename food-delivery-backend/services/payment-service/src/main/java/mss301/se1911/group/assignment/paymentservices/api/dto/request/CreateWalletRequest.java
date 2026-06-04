package mss301.se1911.group.assignment.paymentservices.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import mss301.se1911.group.assignment.paymentservices.domain.vo.OwnerType;

import java.util.UUID;

@Data
public class CreateWalletRequest {

    @NotNull(message = "Owner ID is required")
    private UUID ownerId;

    @NotNull(message = "Owner type is required")
    private OwnerType ownerType;
}
