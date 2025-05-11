package com.asid.order_saga_orchestrator.controller;

import com.asid.order_saga_orchestrator.dto.CreateOrderRequest;
import com.asid.order_saga_orchestrator.orchestrator.OrderSagaOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/saga")
@RequiredArgsConstructor
public class OrchestratorController {

    private final OrderSagaOrchestrator orchestrator;

    @PostMapping("/orders")
    public Mono<ResponseEntity<String>> createOrder(@RequestBody CreateOrderRequest request) {
        return orchestrator.executeSaga(request)
                .map(ResponseEntity::ok);
    }
}
