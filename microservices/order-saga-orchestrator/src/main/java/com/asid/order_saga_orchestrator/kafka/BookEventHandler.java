package com.asid.order_saga_orchestrator.kafka;

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
public class BookEventHandler {

    private final KafkaEventPublisher kafkaPublisher;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "book.reserved.event", groupId = "saga-orchestrator")
    public void onBooksReserved(String message) {
        try {
            log.info("📩 Evento recebido: book.reserved.event");

            Map<String, Object> data = objectMapper.readValue(message, new TypeReference<>() {});
            Long orderId = Long.parseLong(data.get("orderId").toString());

            Object shipping = data.get("shipping");

            if (shipping == null) {
                throw new IllegalArgumentException("Objeto 'shipping' ausente no evento book.reserved.event");
            }

            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("orderId", orderId);
            payloadMap.put("shipping", shipping); // ✅ envia o objeto completo

            String payload = objectMapper.writeValueAsString(payloadMap);
            kafkaPublisher.publish("shipping.create.command", orderId.toString(), payload);
            log.info("📤 Comando publicado: shipping.create.command");

        } catch (Exception e) {
            log.error("❌ Erro ao processar book.reserved.event", e);
        }
    }


    @KafkaListener(topics = "book.reservation.failed.event", groupId = "saga-orchestrator")
    public void onBookReservationFailed(String message) {
        try {
            log.info("📩 Evento recebido: book.reservation.failed.event");

            Map<String, Object> data = objectMapper.readValue(message, new TypeReference<>() {});
            Long orderId = Long.parseLong(data.get("orderId").toString());

            // ROLLBACK: cancelar encomenda no order-service
            kafkaPublisher.publish("order.cancel.command", orderId.toString(), message);
            log.info("📤 Comando publicado: order.cancel.command");

        } catch (Exception e) {
            log.error("❌ Erro ao processar book.reservation.failed.event", e);
        }
    }

    @KafkaListener(topics = "shipping.failed.event", groupId = "saga-orchestrator")
    public void onShippingFailed(String message) {
        try {
            log.info("📩 Evento recebido: shipping.failed.event");

            Map<String, Object> data = objectMapper.readValue(message, new TypeReference<>() {});
            Long orderId = Long.parseLong(data.get("orderId").toString());
            List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("items");

            // ROLLBACK: devolver livros
            String rollbackPayload = objectMapper.writeValueAsString(Map.of(
                "orderId", orderId,
                "items", items
            ));

            kafkaPublisher.publish("book.rollback.command", orderId.toString(), rollbackPayload);
            log.info("📤 Comando publicado: book.rollback.command");

            // Cancelar encomenda
            kafkaPublisher.publish("order.cancel.command", orderId.toString(), message);

        } catch (Exception e) {
            log.error("❌ Erro ao processar shipping.failed.event", e);
        }
    }

}
