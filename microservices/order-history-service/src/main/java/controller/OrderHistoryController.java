package main.java.orderhistoryservice.controller;

import main.java.orderhistoryservice.dto.OrderDetailsResponse;
import main.java.orderhistoryservice.entity.OrderHistory;
import main.java.orderhistoryservice.entity.ShippingInfo;
import main.java.orderhistoryservice.repository.OrderHistoryRepository;
import main.java.orderhistoryservice.repository.ShippingInfoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
public class OrderHistoryController {

    private final OrderHistoryRepository repository;
    private final ShippingInfoRepository shippingInfoRepository;

    @GetMapping("/{userId}")
    public List<OrderHistory> getOrders(
            @PathVariable Long userId,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        return repository.findByUserIdAndOrderDateBetween(userId, from, to);
    }

    @GetMapping("/{userId}/details")
    public List<OrderDetailsResponse> getDetailedOrders(
            @PathVariable Long userId,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        List<OrderHistory> orders = repository.findByUserIdAndOrderDateBetween(userId, from, to);
        return orders.stream().map(order -> {
            OrderDetailsResponse response = new OrderDetailsResponse();
            response.setOrderId(order.getOrderId());
            response.setOrderDate(order.getOrderDate());
            response.setTotalPrice(order.getTotalPrice());
            response.setShippingAddress(order.getShippingAddress());

            try {
                List<OrderDetailsResponse.BookInfo> books = Arrays.asList(
                        new ObjectMapper().readValue(order.getBooksJson(), OrderDetailsResponse.BookInfo[].class)
                );
                response.setBooks(books);
            } catch (Exception e) {
                response.setBooks(List.of());
            }

            // 🔽 Adiciona os dados do Shipping, se existir
            ShippingInfo info = shippingInfoRepository.findById(order.getShippingId()).orElse(null);
            if (info != null) {
                OrderDetailsResponse.Shipping shipping = new OrderDetailsResponse.Shipping();
                shipping.setFirstName(info.getFirstName());
                shipping.setLastName(info.getLastName());
                shipping.setAddress(info.getAddress());
                shipping.setCity(info.getCity());
                shipping.setEmail(info.getEmail());
                shipping.setPostalCode(info.getPostalCode());
                response.setShipping(shipping);
            }

            return response;
        }).toList();
    }
}
