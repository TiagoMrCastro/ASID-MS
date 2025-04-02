package com.gateway.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class OrderDto {
    private Long id;
    private Date orderDate;
    private double totalPrice;
    private Long userId;
    private Long shippingId;
    private List<Long> bookIds;
}
