package MicroServices.OrderService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingDto {
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String email;
    private String postalCode;
}
