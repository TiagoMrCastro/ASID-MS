package MicroServices.ShippingService.service;

import MicroServices.ShippingService.entity.OutboxEvent;
import MicroServices.ShippingService.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventScheduler {

    private final OutboxEventRepository outboxRepo;
    private final KafkaEventPublisher publisher;

    @Scheduled(fixedRate = 5000)
    public void publishPendingEvents() {
        List<OutboxEvent> events = outboxRepo.findByPublishedFalse();
        for (OutboxEvent e : events) {
            try {
                publisher.publish("shipping.events", e.getAggregateId(), e.getPayload());
                e.setPublished(true);
                outboxRepo.save(e);
                log.info("✅ Evento publicado do shipping: {}", e.getAggregateId());
            } catch (Exception ex) {
                log.error("❌ Falha ao publicar evento {}", e.getAggregateId(), ex);
            }
        }
    }
}
