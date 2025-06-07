package com.asid.order_saga_orchestrator.kafka;

import com.asid.order_saga_orchestrator.dto.CartItemDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderSagaEventHandler {

    private final KafkaEventPublisher kafkaPublisher;
    private final ObjectMapper objectMapper;

   @KafkaListener(topics = "order.created.event", groupId = "saga-orchestrator")
    public void onOrderCreated(String message) {
        try {
            log.info("📩 Evento recebido: order.created.event");

            Map<String, Object> data = objectMapper.readValue(message, new TypeReference<>() {});
            Long orderId = Long.parseLong(data.get("orderId").toString());
            List<CartItemDto> items = objectMapper.convertValue(data.get("items"), new TypeReference<>() {});
            Object shipping = data.get("shipping"); 

            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("orderId", orderId);
            payloadMap.put("items", items);
            if (shipping != null) {
                payloadMap.put("shipping", shipping);
            }

            String payload = objectMapper.writeValueAsString(payloadMap);
            kafkaPublisher.publish("book.reserve.command", orderId.toString(), payload);
            log.info("📤 Comando publicado: book.reserve.command");

        } catch (Exception e) {
            log.error("❌ Erro ao processar order.created.event", e);
        }
    }


}
