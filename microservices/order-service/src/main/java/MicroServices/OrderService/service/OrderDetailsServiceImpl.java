package MicroServices.OrderService.service;

import java.util.List;

import MicroServices.OrderService.dto.OrderResponseDto;
import MicroServices.OrderService.entity.Orders;
import MicroServices.OrderService.entity.OrderDetails;
import MicroServices.OrderService.repository.OrderDetailsRepository;
import MicroServices.OrderService.repository.OrdersRepository;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderDetailsServiceImpl implements OrderDetailsService {

    private final OrdersRepository ordersRepository;
    private final OrderDetailsRepository orderDetailsRepository;

    public OrderResponseDto getOrderWithBooks(Long orderId) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        List<OrderDetails> details = orderDetailsRepository.findByOrderId(orderId);

        List<Long> bookIds = details.stream()
                .map(OrderDetails::getBookId)
                .toList();

        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setUserId(order.getUserId());
        dto.setShippingId(order.getShippingId());
        dto.setBookIds(bookIds);
        dto.setSagaStatus(order.getSagaStatus().name());

        return dto;
    }

    @Override
    public OrderDetails createOrderDetails(OrderDetails orderDetails) {
        return orderDetailsRepository.save(orderDetails);
    }
}
