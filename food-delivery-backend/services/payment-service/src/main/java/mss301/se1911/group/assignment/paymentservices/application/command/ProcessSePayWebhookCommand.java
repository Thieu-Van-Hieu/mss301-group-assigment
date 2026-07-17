package mss301.se1911.group.assignment.paymentservices.application.command;

import java.math.BigDecimal;

/**
 * Command representing a webhook payload from SePay.
 */
public record ProcessSePayWebhookCommand(
        String authHeader,
        Long id,
        String gateway,
        String transactionDate,
        String accountNumber,
        String code,
        String content,
        String transferType,
        BigDecimal transferAmount,
        BigDecimal accumulated,
        String subAccount,
        String referenceCode,
        String description
) {
}
