package mss301.se1911.group.assignment.customerservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    @GetMapping("/{id}/validate")
    public ResponseEntity<Void> validateCustomer(@PathVariable("id") UUID id) {
        log.info("Validating customer with ID: {}", id);
        
        // Simulating customer validation failure:
        // If customer ID is "00000000-0000-0000-0000-000000000000", return 404 (invalid).
        if (id.toString().equals("00000000-0000-0000-0000-000000000000")) {
            log.warn("Customer validation failed for ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        
        log.info("Customer validated successfully for ID: {}", id);
        return ResponseEntity.ok().build();
    }
}
