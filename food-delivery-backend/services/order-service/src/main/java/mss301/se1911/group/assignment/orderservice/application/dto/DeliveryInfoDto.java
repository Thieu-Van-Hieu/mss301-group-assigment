package mss301.se1911.group.assignment.orderservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryInfoDto {
    private String address;
    private double latitude;
    private double longitude;
    private String phone;
}
