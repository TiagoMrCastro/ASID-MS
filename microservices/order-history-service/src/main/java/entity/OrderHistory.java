package main.java.orderhistoryservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "order_history")
@Data
public class OrderHistory {

    @Id
    private Long orderId;

    private Long userId;

    private LocalDate orderDate;

    private Double totalPrice;

    private String shippingAddress; 
    
    private Long shippingId;

    @Lob
    private String booksJson; // detalhes dos livros como JSON
}
