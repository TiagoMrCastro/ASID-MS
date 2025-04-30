package com.example.orderhistoryservice.kafka;

import com.example.orderhistoryservice.entity.OrderHistory;
import com.example.orderhistoryservice.repository.OrderHistoryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventsConsumer {

    private final OrderHistoryRepository repository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order.events", groupId = "order-history-group")
    public void consume(String message) {
        log.info("📥 Mensagem Kafka recebida: {}", message);
        try {
            JsonNode node = objectMapper.readTree(message);

            OrderHistory order = new OrderHistory();
            order.setOrderId(node.get("orderId").asLong());
            order.setUserId(node.get("userId").asLong());

            String rawDate = node.get("orderDate").asText(); // ISO: "2025-04-29T23:55:48.263+00:00"
            order.setOrderDate(LocalDate.parse(rawDate.substring(0, 10)));

            order.setTotalPrice(node.get("totalPrice").asDouble());

            order.setShippingAddress("N/A");
            if (node.has("shippingAddress")) {
                order.setShippingAddress(node.get("shippingAddress").asText());
            }

            if (node.has("items")) {
                String booksJson = objectMapper.writeValueAsString(node.get("items"));
                order.setBooksJson(booksJson);
            } else {
                order.setBooksJson("[]");
            }

            repository.save(order);
            log.info("✅ OrderHistory salvo: {}", order.getOrderId());

        } catch (Exception e) {
            log.error("❌ Erro ao processar mensagem Kafka: {}", message, e);
        }
    }
}
