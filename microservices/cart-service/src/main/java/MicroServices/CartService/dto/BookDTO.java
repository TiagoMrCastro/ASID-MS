package MicroServices.CartService.dto;

import lombok.Data;

@Data
public class BookDTO {
    private Long id;
    private String title;
    private double price;
    private int quantity;
}