package com.gateway.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CartItemResponse {
    @JsonProperty("id")
    private Long cartItemId;
    private Long bookId;
    private String bookTitle;
    private Integer quantity;
    private Double price;
    private Double subtotal;
    private String userName;
}
