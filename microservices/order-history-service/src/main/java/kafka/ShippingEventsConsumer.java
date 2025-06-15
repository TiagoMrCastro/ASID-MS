package main.java.orderhistoryservice.kafka;

import main.java.orderhistoryservice.entity.ShippingInfo;
import main.java.orderhistoryservice.repository.ShippingInfoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShippingEventsConsumer {

    private final ShippingInfoRepository repository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "shipping.events", groupId = "order-history-group")
    public void consume(String message) {
        log.info("📥 Evento de shipping recebido: {}", message);
        try {
            ShippingInfo info = objectMapper.readValue(message, ShippingInfo.class);
            repository.save(info);
            log.info("✅ Shipping info salvo: {}", info.getShippingId());
        } catch (Exception e) {
            log.error("❌ Erro ao processar evento de shipping", e);
        }
    }
}
