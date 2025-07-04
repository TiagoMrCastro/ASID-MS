package MicroServices.OrderService.service;

import org.springframework.stereotype.Service;

import MicroServices.OrderService.dto.OrderResponseDto;
import MicroServices.OrderService.entity.OrderDetails;

@Service
public interface OrderDetailsService {
    OrderDetails createOrderDetails(OrderDetails orderDetails);

    OrderResponseDto getOrderWithBooks(Long orderId);
}
