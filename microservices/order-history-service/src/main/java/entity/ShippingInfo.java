package main.java.orderhistoryservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "shipping_info")
@Data
public class ShippingInfo {

    @Id
    private Long shippingId;

    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String email;
    private String postalCode;
}
