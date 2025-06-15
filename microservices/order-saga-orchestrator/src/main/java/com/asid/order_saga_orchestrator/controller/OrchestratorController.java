package com.asid.order_saga_orchestrator.controller;

import com.asid.order_saga_orchestrator.dto.CreateOrderRequest;
import com.asid.order_saga_orchestrator.kafka.KafkaEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/saga")
@RequiredArgsConstructor
public class OrchestratorController {

    private final KafkaEventPublisher kafkaPublisher;
    private final ObjectMapper objectMapper;

    @PostMapping("/orders")
    public ResponseEntity<Long> startSaga(@RequestBody CreateOrderRequest request) {
        try {
            // Envia o evento Kafka
            String payload = objectMapper.writeValueAsString(request);
            kafkaPublisher.publish("order.create.command", request.getUserId().toString(), payload);

            // Tenta obter o orderId por com retries
            RestTemplate restTemplate = new RestTemplate();
            Long orderId = null;
            int attempts = 0;

            while (orderId == null && attempts < 5) {
                Thread.sleep(300); // Aguarda entre tentativas
                try {
                    orderId = restTemplate.getForObject(
                            "http://order-service:8080/orders/latest/" + request.getUserId(),
                            Long.class
                    );
                } catch (Exception ex) {
                    // Pode falhar se ainda não persistiu, tenta novamente
                    orderId = null;
                }
                attempts++;
            }

            if (orderId != null) {
                return ResponseEntity.ok(orderId);
            } else {
                return ResponseEntity.status(504).body(0L); // Gateway Timeout
            }

        } catch (Exception e) {
            // Falha geral no início da saga
            return ResponseEntity.internalServerError().body(0L);
        }
    }
}
