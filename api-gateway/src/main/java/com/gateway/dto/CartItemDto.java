package com.gateway.dto;

import lombok.Data;

@Data
public class CartItemDto {
    private Long userId;
    private Long bookId;
    private Integer quantity;
    private Double price;
}
