package mss301.se1911.group.assignment.paymentservices.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.paymentservices.api.dto.request.CreateWalletRequest;
import mss301.se1911.group.assignment.paymentservices.api.dto.request.TopUpRequest;
import mss301.se1911.group.assignment.paymentservices.api.dto.response.ApiResponse;
import mss301.se1911.group.assignment.paymentservices.api.dto.response.PaymentResponse;
import mss301.se1911.group.assignment.paymentservices.api.dto.response.WalletLedgerResponse;
import mss301.se1911.group.assignment.paymentservices.api.dto.response.WalletResponse;
import mss301.se1911.group.assignment.paymentservices.application.command.CreateWalletCommand;
import mss301.se1911.group.assignment.paymentservices.application.command.InitiateTopUpCommand;
import mss301.se1911.group.assignment.paymentservices.application.usecase.payment.GetSePayQrUrlUseCase;
import mss301.se1911.group.assignment.paymentservices.application.usecase.wallet.CreateWalletUseCase;
import mss301.se1911.group.assignment.paymentservices.application.usecase.wallet.GetWalletByIdUseCase;
import mss301.se1911.group.assignment.paymentservices.application.usecase.wallet.GetWalletByOwnerUseCase;
import mss301.se1911.group.assignment.paymentservices.application.usecase.wallet.GetWalletLedgerUseCase;
import mss301.se1911.group.assignment.paymentservices.application.usecase.wallet.InitiateTopUpUseCase;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction.PaymentMethod;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction.PaymentGateway;
import mss301.se1911.group.assignment.paymentservices.domain.entity.Wallet;
import mss301.se1911.group.assignment.paymentservices.domain.entity.WalletLedger;
import mss301.se1911.group.assignment.commonsecurity.filter.UserPrincipal;
import mss301.se1911.group.assignment.paymentservices.domain.vo.OwnerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final CreateWalletUseCase createWalletUseCase;
    private final GetWalletByOwnerUseCase getWalletByOwnerUseCase;
    private final GetWalletByIdUseCase getWalletByIdUseCase;
    private final GetWalletLedgerUseCase getWalletLedgerUseCase;
    private final InitiateTopUpUseCase initiateTopUpUseCase;
    private final GetSePayQrUrlUseCase getSePayQrUrlUseCase;


    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    public ResponseEntity<ApiResponse<WalletResponse>> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        CreateWalletCommand command = new CreateWalletCommand(request.getOwnerId(), request.getOwnerType());
        Wallet wallet = createWalletUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Wallet created", mapToResponse(wallet)));
    }

    /**
     * IDOR Protection: Only the wallet owner or an admin can query a wallet by owner ID.
     * authentication.name comes from the JWT subject (or X-User-Id header after gateway processing).
     */
    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #a0.toString()")
    public ResponseEntity<ApiResponse<WalletResponse>> getWalletByOwner(
            @PathVariable("ownerId") UUID ownerId,
            @RequestParam("ownerType") OwnerType ownerType) {
        Wallet wallet = getWalletByOwnerUseCase.execute(ownerId, ownerType);
        return ResponseEntity.ok(ApiResponse.success(mapToResponse(wallet)));
    }

    /**
     * IDOR Protection: Only admins can directly query by walletId.
     * For non-admin users, use /owner/{ownerId} endpoint instead.
     */
    @GetMapping("/{walletId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<WalletResponse>> getWalletById(@PathVariable("walletId") UUID walletId) {
        Wallet wallet = getWalletByIdUseCase.execute(walletId);
        return ResponseEntity.ok(ApiResponse.success(mapToResponse(wallet)));
    }

    /**
     * IDOR Protection: Only admins can directly query ledger by walletId.
     * For non-admin users, use a dedicated /owner/{ownerId}/ledgers endpoint (future).
     */
    @GetMapping("/{walletId}/ledgers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<WalletLedgerResponse>>> getWalletLedger(
            @PathVariable("walletId") UUID walletId,
            Pageable pageable) {
            
        Page<WalletLedger> ledgers = getWalletLedgerUseCase.execute(walletId, pageable);
        Page<WalletLedgerResponse> responsePage = ledgers.map(this::mapToLedgerResponse);
        
        return ResponseEntity.ok(ApiResponse.success(responsePage));
    }

    /**
     * Initiates a wallet top-up. Customer pays via an external gateway (SEPAY/PAYOS/MOMO),
     * and upon webhook confirmation, the wallet is credited.
     * IDOR Protection: customerId is extracted from the JWT principal.
     */
    @PostMapping("/top-up")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaymentResponse>> topUpWallet(
            @Valid @RequestBody TopUpRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest) {

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        UUID customerId = UUID.fromString(principal.id());

        InitiateTopUpCommand command = new InitiateTopUpCommand(
                customerId,
                request.getAmount(),
                request.getGatewayMethod(),
                getIpAddress(httpRequest)
        );

        PaymentTransaction transaction = initiateTopUpUseCase.execute(command);
        PaymentResponse response = mapToPaymentResponse(transaction);

        if (request.getGatewayMethod() == PaymentGateway.SEPAY) {
            response.setPayUrl(getSePayQrUrlUseCase.execute(transaction.getId()));
        }
        response.setStatus("PROCESSING");

        return ResponseEntity.ok(ApiResponse.success("Top-up initiated", response));
    }

    private String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    private PaymentResponse mapToPaymentResponse(PaymentTransaction tx) {
        return PaymentResponse.builder()
                .id(tx.getId())
                .orderId(tx.getOrderId())
                .customerId(tx.getCustomerId())
                .amount(tx.getMoney().getAmount())
                .currency(tx.getMoney().getCurrency())
                .paymentMethod(tx.getPaymentMethod() != null ? tx.getPaymentMethod().name() : null)
                .paymentGateway(tx.getPaymentGateway() != null ? tx.getPaymentGateway().name() : null)
                .status(tx.getStatus().name())
                .paidAt(tx.getPaidAt())
                .createdAt(tx.getCreatedAt())
                .build();
    }

    private WalletResponse mapToResponse(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .ownerId(wallet.getOwner().getOwnerId())
                .ownerType(wallet.getOwner().getOwnerType().name())
                .balance(wallet.getBalance().getAmount())
                .currency(wallet.getBalance().getCurrency())
                .status(wallet.getStatus().name())
                .build();
    }

    private WalletLedgerResponse mapToLedgerResponse(WalletLedger ledger) {
        return WalletLedgerResponse.builder()
                .id(ledger.getId())
                .transactionRefId(ledger.getTransactionRefId())
                .entryType(ledger.getEntryType().name())
                .amount(ledger.getAmount())
                .balanceBefore(ledger.getBalanceBefore())
                .balanceAfter(ledger.getBalanceAfter())
                .description(ledger.getDescription())
                .createdAt(ledger.getCreatedAt())
                .build();
    }
}
