// dto/CreateOrderRequest.java
package MicroServices.OrderService.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {
    private Long userId;
    private Long shippingId;
    private List<BookOrderItem> items;
    
    @Data
    public static class BookOrderItem {
        private Long bookId;
        private int quantity;
        private double price;
    }
}
