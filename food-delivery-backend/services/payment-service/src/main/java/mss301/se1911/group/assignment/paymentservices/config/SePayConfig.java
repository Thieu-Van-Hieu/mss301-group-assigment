package mss301.se1911.group.assignment.paymentservices.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "payment.sepay")
@Getter
@Setter
public class SePayConfig {

    /**
     * Bank name or BIN (e.g. MBBank, VCB, 970422).
     */
    private String bankName;

    /**
     * Bank account number receiving the money.
     */
    private String accountNumber;

    /**
     * Secret API Key to validate incoming webhooks from SePay.
     */
    private String apiKey;

    /**
     * Base URL for VietQR generation (e.g., https://qr.sepay.vn/img)
     */
    private String qrUrl = "https://qr.sepay.vn/img";

    /**
     * Prefix for the transfer content.
     */
    private String contentPrefix = "Thanh toan ";
}
