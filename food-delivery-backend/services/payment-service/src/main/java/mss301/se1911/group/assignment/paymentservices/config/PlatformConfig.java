package mss301.se1911.group.assignment.paymentservices.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "payment.platform")
public class PlatformConfig {

    /**
     * Commission rate charged to restaurant (e.g., 0.15 = 15%).
     */
    private double restaurantCommissionRate = 0.15;

    /**
     * Commission rate charged to driver (e.g., 0.20 = 20%).
     */
    private double driverCommissionRate = 0.20;
}
