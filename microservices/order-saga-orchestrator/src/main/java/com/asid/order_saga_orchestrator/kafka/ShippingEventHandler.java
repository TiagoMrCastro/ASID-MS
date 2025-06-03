package com.asid.order_saga_orchestrator.kafka;

import com.asid.order_saga_orchestrator.kafka.KafkaEventPublisher;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingEventHandler {

    private final KafkaEventPublisher kafkaPublisher;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "shipping.created.event", groupId = "saga-orchestrator")
    public void onShippingCreated(String message) {
        try {
            log.info("📩 Evento recebido: shipping.created.event");

            Map<String, Object> data = objectMapper.readValue(message, new TypeReference<>() {});
            Long shippingId = Long.parseLong(data.get("shippingId").toString());
            Long orderId = Long.parseLong(data.get("aggregateId") != null ?
                    data.get("aggregateId").toString() : data.get("orderId").toString());

            // Atualizar a encomenda com shippingId
            var updatePayload = objectMapper.writeValueAsString(Map.of(
                    "orderId", orderId,
                    "shippingId", shippingId
            ));
            kafkaPublisher.publish("order.update.shipping", orderId.toString(), updatePayload);
            log.info("📤 Comando enviado: order.update.shipping");

            // Finalizar a encomenda
            var completePayload = objectMapper.writeValueAsString(Map.of(
                    "orderId", orderId,
                    "status", "COMPLETED"
            ));
            kafkaPublisher.publish("order.complete.command", orderId.toString(), completePayload);
            log.info("📤 Comando enviado: order.complete.command");

            // Evento final da saga
            var sagaSuccess = objectMapper.writeValueAsString(Map.of(
                    "orderId", orderId,
                    "shippingId", shippingId,
                    "status", "COMPLETED"
            ));
            kafkaPublisher.publish("order.saga.success", orderId.toString(), sagaSuccess);

        } catch (Exception e) {
            log.error("❌ Erro ao processar shipping.created.event", e);
        }
    }
}
