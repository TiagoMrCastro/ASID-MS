package main.java.orderhistoryservice.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.java.orderhistoryservice.entity.OrderHistory;
import main.java.orderhistoryservice.repository.OrderHistoryRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SagaStatusConsumer {

    private final OrderHistoryRepository repository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = {"order.saga.success", "order.saga.failure"}, groupId = "order-history-group")
    public void consumeSagaStatus(String message) {
        log.info("📥 Saga Status Kafka recebido: {}", message);
        try {
            JsonNode node = objectMapper.readTree(message);
            Long orderId = node.get("orderId").asLong();
            String status = node.get("status").asText(); // COMPLETED ou FAILED
            String reason = node.has("reason") ? node.get("reason").asText() : null;

            OrderHistory order = repository.findById(orderId).orElse(null);
            if (order != null) {
                order.setSagaStatus(status);
                repository.save(order);
                log.info("✅ Saga status '{}' salvo para orderId={}", status, orderId);
            } else {
                log.warn("⚠️ OrderId {} não encontrado para salvar status '{}'", orderId, status);
            }

        } catch (Exception e) {
            log.error("❌ Erro ao processar saga status", e);
        }
    }
}
