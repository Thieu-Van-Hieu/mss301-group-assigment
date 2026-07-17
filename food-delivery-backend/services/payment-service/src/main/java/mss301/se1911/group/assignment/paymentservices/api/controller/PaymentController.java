package mss301.se1911.group.assignment.paymentservices.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.paymentservices.api.dto.request.InitiatePaymentRequest;
import mss301.se1911.group.assignment.paymentservices.api.dto.response.ApiResponse;
import mss301.se1911.group.assignment.paymentservices.api.dto.response.PaymentResponse;
import mss301.se1911.group.assignment.paymentservices.application.command.ConfirmCodPaymentCommand;
import mss301.se1911.group.assignment.paymentservices.application.command.InitiatePaymentCommand;
import mss301.se1911.group.assignment.paymentservices.application.command.ProcessSePayWebhookCommand;
import mss301.se1911.group.assignment.paymentservices.application.usecase.payment.ConfirmCodPaymentUseCase;
import mss301.se1911.group.assignment.paymentservices.application.usecase.payment.GetPaymentByIdUseCase;
import mss301.se1911.group.assignment.paymentservices.application.usecase.payment.GetPaymentByOrderIdUseCase;
import mss301.se1911.group.assignment.paymentservices.application.usecase.payment.GetSePayQrUrlUseCase;
import mss301.se1911.group.assignment.paymentservices.application.usecase.payment.InitiatePaymentUseCase;
import mss301.se1911.group.assignment.paymentservices.application.usecase.payment.ProcessSePayWebhookUseCase;
import mss301.se1911.group.assignment.paymentservices.application.usecase.wallet.PayWalletUseCase;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction.PaymentMethod;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction.PaymentGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final InitiatePaymentUseCase initiatePaymentUseCase;
    private final GetSePayQrUrlUseCase getSePayQrUrlUseCase;
    private final GetPaymentByIdUseCase getPaymentByIdUseCase;
    private final GetPaymentByOrderIdUseCase getPaymentByOrderIdUseCase;
    private final ConfirmCodPaymentUseCase confirmCodPaymentUseCase;
    private final ProcessSePayWebhookUseCase processSePayWebhookUseCase;
    private final PayWalletUseCase payWalletUseCase;

    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiatePayment(
            @Valid @RequestBody InitiatePaymentRequest request,
            HttpServletRequest httpRequest) {
            
        InitiatePaymentCommand command = new InitiatePaymentCommand(
                request.getOrderId(),
                request.getCustomerId(),
                request.getAmount(),
                request.getPaymentMethod(),
                request.getPaymentGateway(),
                getIpAddress(httpRequest)
        );
        
        PaymentTransaction transaction = initiatePaymentUseCase.execute(command);

        PaymentResponse response = mapToResponse(transaction);
        
        if (request.getPaymentMethod() == PaymentMethod.BANK_TRANSFER) {
            if (request.getPaymentGateway() == PaymentGateway.SEPAY) {
                String qrUrl = getSePayQrUrlUseCase.execute(transaction.getId());
                response.setPayUrl(qrUrl);
                response.setStatus("PROCESSING");
            }
        } else if (request.getPaymentMethod() == PaymentMethod.WALLET) {
            // Wallet payment is instant — debit and mark PAID immediately
            PaymentTransaction paid = payWalletUseCase.execute(transaction);
            response = mapToResponse(paid);
        }

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{paymentTxId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(@PathVariable("paymentTxId") UUID paymentTxId) {
        PaymentTransaction transaction = getPaymentByIdUseCase.execute(paymentTxId);
        return ResponseEntity.ok(ApiResponse.success(mapToResponse(transaction)));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByOrder(@PathVariable("orderId") UUID orderId) {
        PaymentTransaction transaction = getPaymentByOrderIdUseCase.execute(orderId);
        return ResponseEntity.ok(ApiResponse.success(mapToResponse(transaction)));
    }

    @PostMapping("/{orderId}/cod/confirm")
    public ResponseEntity<ApiResponse<PaymentResponse>> confirmCodPayment(@PathVariable("orderId") UUID orderId) {
        ConfirmCodPaymentCommand command = new ConfirmCodPaymentCommand(orderId);
        PaymentTransaction transaction = confirmCodPaymentUseCase.execute(command);
        return ResponseEntity.ok(ApiResponse.success("COD payment confirmed", mapToResponse(transaction)));
    }

    @PostMapping("/sepay-webhook")
    public ResponseEntity<String> sePayWebhook(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody ProcessSePayWebhookCommand commandBody) {
        try {
            // SePay often uses Authorization header. If they use Apikey, we construct a full command combining both.
            // Using a new record instance with auth header attached.
            ProcessSePayWebhookCommand fullCommand = new ProcessSePayWebhookCommand(
                    authHeader,
                    commandBody.id(),
                    commandBody.gateway(),
                    commandBody.transactionDate(),
                    commandBody.accountNumber(),
                    commandBody.code(),
                    commandBody.content(),
                    commandBody.transferType(),
                    commandBody.transferAmount(),
                    commandBody.accumulated(),
                    commandBody.subAccount(),
                    commandBody.referenceCode(),
                    commandBody.description()
            );
            
            processSePayWebhookUseCase.execute(fullCommand);
            
            // Return 200 OK with success object to let SePay know it was received
            return ResponseEntity.ok("{\"success\":true}");
        } catch (Exception e) {
            // SePay documentation recommends returning 200 OK even if validation fails so they don't retry endlessly,
            // unless it's a 401 Unauthorized for bad API token, but usually 200 OK is safer to stop retries
            // if we handled it and rejected it intentionally. We'll return 200 OK with error body.
            return ResponseEntity.ok("{\"success\":false, \"message\":\"" + e.getMessage() + "\"}");
        }
    }


    private String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    private PaymentResponse mapToResponse(PaymentTransaction tx) {
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
}
