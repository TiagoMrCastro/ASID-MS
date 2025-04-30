// controller/OrderCommandController.java
package MicroServices.OrderService.controller;

import MicroServices.OrderService.dto.CreateOrderRequest;
import MicroServices.OrderService.entity.Orders;
import MicroServices.OrderService.service.OrderCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderCommandController {

    private final OrderCommandService orderService;

    @PostMapping
    public ResponseEntity<Orders> createOrder(@RequestBody CreateOrderRequest request) {
        Orders order = orderService.createOrder(request);
        return ResponseEntity.ok(order);
    }
}
