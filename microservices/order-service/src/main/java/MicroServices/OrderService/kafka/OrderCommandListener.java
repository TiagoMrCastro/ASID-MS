package MicroServices.OrderService.kafka;

import MicroServices.OrderService.dto.CreateOrderRequest;
import MicroServices.OrderService.entity.Orders;
import MicroServices.OrderService.enums.SagaStatus;
import MicroServices.OrderService.service.OrderCommandService;
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
public class OrderCommandListener {

    private final OrderCommandService orderService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order.create.command", groupId = "order-service")
    public void handleCreateOrder(String message) {
        try {
            CreateOrderRequest request = objectMapper.readValue(message, CreateOrderRequest.class);
            Orders order = orderService.createOrder(request);
            log.info("✅ Encomenda criada com ID {}", order.getId());

            // ✅ Corrigido: incluir shipping no evento
            var response = new CreateOrderResponse(order.getId(), request.getItems(), request.getShipping());
            String payload = objectMapper.writeValueAsString(response);
            orderService.sendKafka("order.created.event", order.getId().toString(), payload);

        } catch (Exception e) {
            log.error("❌ Erro ao processar order.create.command", e);
        }
    }

    // ✅ Corrigido: novo campo shipping
    record CreateOrderResponse(Long orderId, Object items, Object shipping) {}

    @KafkaListener(topics = "order.update.shipping", groupId = "order-service")
    public void handleUpdateShipping(String message) {
        try {
            Map<String, Object> data = objectMapper.readValue(message, new TypeReference<>() {});
            Long orderId = Long.parseLong(data.get("orderId").toString());
            Long shippingId = Long.parseLong(data.get("shippingId").toString());

            orderService.updateShippingId(orderId, shippingId);
            log.info("🚚 Shipping ID atualizado na encomenda {}", orderId);

        } catch (Exception e) {
            log.error("❌ Erro ao processar order.update.shipping", e);
        }
    }

    @KafkaListener(topics = "order.complete.command", groupId = "order-service")
    public void handleCompleteOrder(String message) {
        try {
            Map<String, Object> data = objectMapper.readValue(message, new TypeReference<>() {});
            Long orderId = Long.parseLong(data.get("orderId").toString());

            orderService.updateSagaStatus(orderId, SagaStatus.COMPLETED);
            log.info("✅ Encomenda {} marcada como COMPLETED", orderId);

        } catch (Exception e) {
            log.error("❌ Erro ao processar order.complete.command", e);
        }
    }

    @KafkaListener(topics = "order.cancel.command", groupId = "order-service")
    public void handleCancelOrder(String message) {
        try {
            Map<String, Object> data = objectMapper.readValue(message, new TypeReference<>() {});
            Long orderId = Long.parseLong(data.get("orderId").toString());

            orderService.updateSagaStatus(orderId, SagaStatus.FAILED);
            log.info("🚫 Encomenda {} cancelada com status FAILED", orderId);

        } catch (Exception e) {
            log.error("❌ Erro ao processar order.cancel.command", e);
        }
    }

    @KafkaListener(topics = "shipping.created.event", groupId = "order-service")
    public void handleShippingCreated(String message) {
        try {
            Map<String, Object> data = objectMapper.readValue(message, new TypeReference<>() {});
            Long orderId = Long.parseLong(data.get("orderId").toString());
            Long shippingId = Long.parseLong(data.get("shippingId").toString());

            orderService.updateShippingId(orderId, shippingId);
            log.info("📦 Shipping ID {} associado à encomenda {}", shippingId, orderId);
        } catch (Exception e) {
            log.error("❌ Erro ao processar shipping.created.event", e);
        }
    }

}
