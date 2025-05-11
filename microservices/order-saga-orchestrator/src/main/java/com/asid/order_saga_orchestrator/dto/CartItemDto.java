package com.asid.order_saga_orchestrator.dto;

import lombok.Data;

@Data
public class CartItemDto {
    private Long bookId;
    private int quantity;
    private double price;
}
