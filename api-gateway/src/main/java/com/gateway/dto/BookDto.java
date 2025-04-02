package com.gateway.dto;

import lombok.Data;

@Data
public class BookDto {
    private Long id;
    private String title;
    private String isbnNumber;
    private String description;
    private double price;
    private int quantity;
}
