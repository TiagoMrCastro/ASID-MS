package MicroServices.OrderService.service;

import MicroServices.OrderService.entity.OutboxEvent;
import MicroServices.OrderService.repository.OutboxEventRepository;
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
                publisher.publish("order.events", e.getAggregateId(), e.getPayload());
                e.setPublished(true);
                outboxRepo.save(e);
                log.info("Published event ID {}", e.getId());
            } catch (Exception ex) {
                log.error("Failed to publish event {}", e.getId(), ex);
            }
        }
    }
}
