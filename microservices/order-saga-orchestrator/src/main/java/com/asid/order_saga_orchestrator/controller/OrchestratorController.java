package com.asid.order_saga_orchestrator.controller;

import com.asid.order_saga_orchestrator.dto.CreateOrderRequest;
import com.asid.order_saga_orchestrator.kafka.KafkaEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/saga")
@RequiredArgsConstructor
public class OrchestratorController {

    private final KafkaEventPublisher kafkaPublisher;
    private final ObjectMapper objectMapper;

    @PostMapping("/orders")
    public ResponseEntity<String> startSaga(@RequestBody CreateOrderRequest request) {
        try {
            String payload = objectMapper.writeValueAsString(request);
            kafkaPublisher.publish("order.create.command", request.getUserId().toString(), payload);
            return ResponseEntity.ok("Saga iniciada. Evento enviado para criar a encomenda.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("❌ Erro ao iniciar saga: " + e.getMessage());
        }
    }
}
