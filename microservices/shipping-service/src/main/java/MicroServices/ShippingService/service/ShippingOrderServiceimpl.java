package MicroServices.ShippingService.service;

import MicroServices.ShippingService.entity.OutboxEvent;
import MicroServices.ShippingService.entity.ShippingOrder;
import MicroServices.ShippingService.event.ShippingCreatedEvent;
import MicroServices.ShippingService.repository.OutboxEventRepository;
import MicroServices.ShippingService.repository.ShippingOrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ShippingOrderServiceimpl implements ShippingOrderService {

    @Autowired
    private ShippingOrderRepository shippingOrderRepository;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ShippingOrder createShippingOrder(ShippingOrder shippingOrder) {
        // 1. Salva a entidade de shipping
        ShippingOrder saved = shippingOrderRepository.save(shippingOrder);

        // 2. Garante que foi salvo corretamente com ID
        if (saved.getId() == null) {
            log.error("❌ ShippingOrder salvo sem ID, abortando criação de evento.");
            throw new RuntimeException("ShippingOrder salvo sem ID válido.");
        }

        try {
            // 3. Cria o evento com os dados de shipping
            ShippingCreatedEvent eventPayload = new ShippingCreatedEvent(
                    saved.getId(),
                    saved.getFirstName(),
                    saved.getLastName(),
                    saved.getAddress(),
                    saved.getCity(),
                    saved.getEmail(),
                    saved.getPostalCode()
            );

            // 4. Serializa o evento
            String payload = objectMapper.writeValueAsString(eventPayload);

            // 5. Cria e salva evento na outbox
            OutboxEvent event = OutboxEvent.builder()
                    .aggregateId(saved.getId().toString())
                    .aggregateType("Shipping")
                    .type("ShippingCreated")
                    .payload(payload)
                    .published(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxEventRepository.save(event);
            log.info("✅ Evento de shipping salvo na outbox: {}", saved.getId());

        } catch (Exception e) {
            log.error("❌ Erro ao serializar ou salvar evento de shipping", e);
            throw new RuntimeException("Erro ao criar evento de shipping", e);
        }

        return saved;
    }

    @Override
    public void deleteById(Long id) {
        shippingOrderRepository.deleteById(id);
    }

    @Override
    public List<ShippingOrder> getAllShippingOrders() {
        return shippingOrderRepository.findAll();
    }

    @Override
    public Optional<ShippingOrder> getById(Long id) {
        return shippingOrderRepository.findById(id);
    }
}
