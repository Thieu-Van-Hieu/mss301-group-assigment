package mss301.se1911.group.assignment.paymentservices.infrastructure.adapter.sepay;

import mss301.se1911.group.assignment.paymentservices.config.SePayConfig;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction;
import mss301.se1911.group.assignment.paymentservices.domain.exception.SePayValidationException;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SePayAdapter {

    private final SePayConfig sePayConfig;

    // Pattern to extract PaymentTransaction ID from content.
    // Supports standard UUID with hyphens or compact UUID (32-character hex).
    private static final Pattern TX_ID_PATTERN = Pattern.compile(
            "([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12})|\\b([a-fA-F0-9]{32})\\b"
    );

    public SePayAdapter(SePayConfig sePayConfig) {
        this.sePayConfig = sePayConfig;
    }

    /**
     * Generates a VietQR URL using SePay's free QR generation endpoint.
     */
    public String generateQrUrl(PaymentTransaction transaction) {
        try {
            String acc = URLEncoder.encode(sePayConfig.getAccountNumber(), StandardCharsets.UTF_8);
            String bank = URLEncoder.encode(sePayConfig.getBankName(), StandardCharsets.UTF_8);
            String amount = transaction.getMoney().getAmount().toPlainString();
            
            // Content matches what users will type and what SePay will send in Webhook
            String content = sePayConfig.getContentPrefix() + transaction.getId().toString();
            String des = URLEncoder.encode(content, StandardCharsets.UTF_8);

            return String.format("%s?acc=%s&bank=%s&amount=%s&des=%s",
                    sePayConfig.getQrUrl(), acc, bank, amount, des);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate SePay QR URL", e);
        }
    }

    /**
     * Validates the webhook using the provided Apikey header.
     */
    public void validateWebhook(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Apikey ")) {
            throw new SePayValidationException("Missing or invalid Apikey header");
        }

        String token = authHeader.substring(7);
        if (!token.equals(sePayConfig.getApiKey())) {
            throw new SePayValidationException("Invalid SePay Apikey");
        }
    }

    /**
     * Extracts the PaymentTransaction UUID from the transfer content.
     */
    public String extractTxnRef(String content) {
        if (content == null) {
            return null;
        }
        Matcher matcher = TX_ID_PATTERN.matcher(content);
        if (matcher.find()) {
            String standard = matcher.group(1);
            if (standard != null) {
                return standard;
            }
            String compact = matcher.group(2);
            if (compact != null) {
                // Reconstruct standard UUID from 32-character compact hex string
                return compact.replaceFirst(
                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{12})",
                        "$1-$2-$3-$4-$5"
                );
            }
        }
        return null;
    }
}
