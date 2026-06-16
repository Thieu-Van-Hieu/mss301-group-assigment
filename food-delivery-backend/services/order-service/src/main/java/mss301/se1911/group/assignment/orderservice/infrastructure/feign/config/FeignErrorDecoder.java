package mss301.se1911.group.assignment.orderservice.infrastructure.feign.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.orderservice.domain.exception.OrderDomainException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        String requestUrl = response.request().url();
        int status = response.status();

        String errorMessage = "Error calling external service";
        if (response.body() != null) {
            try (InputStream bodyIs = response.body().asInputStream()) {
                byte[] bodyBytes = bodyIs.readAllBytes();
                errorMessage = new String(bodyBytes, StandardCharsets.UTF_8);
                log.error("Feign client call failed to {} with status {}. Body: {}", requestUrl, status, errorMessage);
            } catch (IOException e) {
                log.error("Failed to read error response body from Feign client", e);
            }
        } else {
            log.error("Feign client call failed to {} with status {} (no response body)", requestUrl, status);
        }

        // Identify the service name
        String serviceName = "UNKNOWN-SERVICE";
        if (methodKey != null) {
            if (methodKey.toLowerCase().contains("customer")) {
                serviceName = "CUSTOMER-SERVICE";
            } else if (methodKey.toLowerCase().contains("restaurant")) {
                serviceName = "RESTAURANT-SERVICE";
            } else if (methodKey.toLowerCase().contains("payment")) {
                serviceName = "PAYMENT-SERVICE";
            }
        }
        if (serviceName.equals("UNKNOWN-SERVICE") && requestUrl != null) {
            if (requestUrl.toLowerCase().contains("customer")) {
                serviceName = "CUSTOMER-SERVICE";
            } else if (requestUrl.toLowerCase().contains("restaurant")) {
                serviceName = "RESTAURANT-SERVICE";
            } else if (requestUrl.toLowerCase().contains("payment")) {
                serviceName = "PAYMENT-SERVICE";
            }
        }

        // Parse and clean up error message
        String lowerMessage = errorMessage.toLowerCase();
        String detailMessage = errorMessage;

        if (status == 404) {
            if ("CUSTOMER-SERVICE".equals(serviceName)) {
                detailMessage = "usernotfound";
            } else if ("RESTAURANT-SERVICE".equals(serviceName)) {
                detailMessage = "restaurantnotfound";
            } else if ("PAYMENT-SERVICE".equals(serviceName)) {
                detailMessage = "paymentnotfound";
            } else {
                detailMessage = "resourcenotfound";
            }
        } else {
            if (lowerMessage.contains("user not found") || lowerMessage.contains("usernotfound") ||
                    lowerMessage.contains("customer not found") || lowerMessage.contains("customernotfound")) {
                detailMessage = "usernotfound";
            } else if (lowerMessage.contains("restaurant not found") || lowerMessage.contains("restaurantnotfound")) {
                detailMessage = "restaurantnotfound";
            } else if (lowerMessage.contains("payment not found") || lowerMessage.contains("paymentnotfound")) {
                detailMessage = "paymentnotfound";
            }
        }

        String finalErrorMessage = String.format("[%s] error: %s (status: %d)", serviceName, detailMessage, status);
        return new OrderDomainException(finalErrorMessage);
    }
}
