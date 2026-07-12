package mss301.se1911.group.assignment.paymentservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan("mss301.se1911.group.assignment.paymentservices.config")
public class PaymentServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServicesApplication.class, args);
    }

}
