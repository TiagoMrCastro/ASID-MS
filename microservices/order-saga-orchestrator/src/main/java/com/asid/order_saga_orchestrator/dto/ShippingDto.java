package com.asid.order_saga_orchestrator.dto;

import lombok.Data;

@Data
public class ShippingDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String email;
    private String postalCode;
}