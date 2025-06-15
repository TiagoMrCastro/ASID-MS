package MicroServices.OrderService.controller;

import MicroServices.OrderService.dto.CreateOrderRequest;
import MicroServices.OrderService.entity.Orders;
import MicroServices.OrderService.enums.SagaStatus;
import MicroServices.OrderService.repository.OrdersRepository;
import MicroServices.OrderService.service.OrderCommandService;
import lombok.RequiredArgsConstructor;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderCommandController {

    private final OrderCommandService orderService;
    private final OrdersRepository ordersRepository;

    @PostMapping
    public ResponseEntity<Orders> createOrder(@RequestBody CreateOrderRequest request) {
        Orders order = orderService.createOrder(request);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.updateSagaStatus(orderId, SagaStatus.FAILED);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{orderId}/complete")
    public ResponseEntity<Void> completeOrder(@PathVariable Long orderId) {
        orderService.updateSagaStatus(orderId, SagaStatus.COMPLETED);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{orderId}/shipping/{shippingId}")
    public ResponseEntity<Void> updateShippingId(
            @PathVariable Long orderId,
            @PathVariable Long shippingId) {
        orderService.updateShippingId(orderId, shippingId);
        orderService.updateSagaStatus(orderId, SagaStatus.IN_SHIPPING);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/latest/{userId}")
    public ResponseEntity<Long> getLatestOrderByUser(@PathVariable Long userId) {
        return ordersRepository.findTopByUserIdOrderByOrderDateDesc(userId)
                .map(order -> ResponseEntity.ok(order.getId()))
                .orElse(ResponseEntity.notFound().build());
    }

}
