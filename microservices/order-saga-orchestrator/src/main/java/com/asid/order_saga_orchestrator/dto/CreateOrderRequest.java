package com.asid.order_saga_orchestrator.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    private Long userId;
    private Long shippingId;  
    private List<CartItemDto> items;
    private ShippingDto shipping;
}
