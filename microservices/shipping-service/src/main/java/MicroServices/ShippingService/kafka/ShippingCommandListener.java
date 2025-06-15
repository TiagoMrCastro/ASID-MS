package MicroServices.ShippingService.kafka;

import MicroServices.ShippingService.entity.ShippingOrder;
import MicroServices.ShippingService.repository.ShippingOrderRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingCommandListener {

    private final ShippingOrderRepository shippingRepo;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "shipping.create.command", groupId = "shipping-service")
    public void handleShippingCommand(String message) {
        try {
            log.info("📩 Evento recebido: shipping.create.command");

            if (message == null || message.isBlank()) {
                throw new IllegalArgumentException("Payload vazio!");
            }

            Map<String, Object> data = objectMapper.readValue(message, new TypeReference<>() {});
            if (data == null || !data.containsKey("orderId") || !data.containsKey("shipping")) {
                throw new IllegalArgumentException("Dados incompletos: orderId ou shipping ausentes");
            }

            Long orderId = Long.parseLong(data.get("orderId").toString());

            @SuppressWarnings("unchecked")
            Map<String, Object> shippingData = (Map<String, Object>) data.get("shipping");

            if (shippingData == null) {
                throw new IllegalArgumentException("Objeto shipping está nulo");
            }

            ShippingOrder shipping = new ShippingOrder();
            shipping.setFirstName(shippingData.get("firstName").toString());
            shipping.setLastName(shippingData.get("lastName").toString());
            shipping.setAddress(shippingData.get("address").toString());
            shipping.setCity(shippingData.get("city").toString());
            shipping.setEmail(shippingData.get("email").toString());
            shipping.setPostalCode(shippingData.get("postalCode").toString());

            shipping = shippingRepo.save(shipping);

            var payload = objectMapper.writeValueAsString(Map.of(
                    "orderId", orderId,
                    "shippingId", shipping.getId()
            ));

            kafkaTemplate.send("shipping.created.event", orderId.toString(), payload);
            log.info("📤 Evento publicado: shipping.created.event");
            log.debug("📨 Payload recebido:\n{}", message);

        } catch (Exception e) {
            log.error("❌ Erro ao processar shipping.create.command", e);
        }
    }

}
