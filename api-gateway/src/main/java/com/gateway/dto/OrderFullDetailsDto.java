package com.gateway.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderFullDetailsDto {
    private OrderDto order;
    private UserDto user;
    private ShippingDto shipping;
    private List<BookDto> books;
}
