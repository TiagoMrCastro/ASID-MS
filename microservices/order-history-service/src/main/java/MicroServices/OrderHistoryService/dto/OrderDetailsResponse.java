
package com.example.orderhistoryservice.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrderDetailsResponse {
    private Long orderId;
    private LocalDate orderDate;
    private Double totalPrice;
    private String shippingAddress;
    private List<BookInfo> books;

    @Data
    public static class BookInfo {
        private Long bookId;
        private String title;
        private int quantity;
        private double price;
    }
}
