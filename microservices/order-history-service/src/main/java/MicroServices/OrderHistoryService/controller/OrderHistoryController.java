package com.example.orderhistoryservice.controller;

import com.example.orderhistoryservice.repository.OrderHistoryRepository;
import com.example.orderhistoryservice.dto.OrderDetailsResponse;
import com.example.orderhistoryservice.entity.OrderHistory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
public class OrderHistoryController {

    private final OrderHistoryRepository repository;

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

            return response;
        }).toList();
    }

}
