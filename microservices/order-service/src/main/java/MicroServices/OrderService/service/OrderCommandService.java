package MicroServices.OrderService.service;

import MicroServices.OrderService.dto.CreateOrderRequest;
import MicroServices.OrderService.entity.OrderDetails;
import MicroServices.OrderService.entity.Orders;
import MicroServices.OrderService.entity.OutboxEvent;
import MicroServices.OrderService.enums.SagaStatus;
import MicroServices.OrderService.repository.OrderDetailsRepository;
import MicroServices.OrderService.repository.OrdersRepository;
import MicroServices.OrderService.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCommandService {

    private final OrdersRepository ordersRepo;
    private final OrderDetailsRepository detailsRepo;
    private final OutboxEventRepository outboxRepo;
    private final ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public Orders createOrder(CreateOrderRequest request) {
        Orders order = new Orders();
        order.setOrderDate(new Date());
        order.setUserId(request.getUserId());
        order.setShippingId(null);
        order.setSagaStatus(SagaStatus.PENDING);

        double total = request.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        order.setTotalPrice(total);

        Orders savedOrder = ordersRepo.save(order);

        for (CreateOrderRequest.BookOrderItem item : request.getItems()) {
            OrderDetails detail = new OrderDetails();
            detail.setOrderId(savedOrder.getId());
            detail.setBookId(item.getBookId());
            detail.setQuantity(item.getQuantity());
            detail.setSubTotal(item.getPrice() * item.getQuantity());
            detailsRepo.save(detail);
        }

        return savedOrder;
    }

    @Transactional
    public void updateShippingId(Long orderId, Long shippingId) {
        Orders order = ordersRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setShippingId(shippingId);
        ordersRepo.save(order);
    }

    @Transactional
    public void updateSagaStatus(Long orderId, SagaStatus status) {
        Orders order = ordersRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setSagaStatus(status);
        ordersRepo.save(order);

        if (status == SagaStatus.COMPLETED) {
            try {
                List<OrderDetails> details = detailsRepo.findByOrderId(orderId);

                var items = details.stream().map(detail -> {
                    double pricePerUnit = detail.getQuantity() == 0 ? 0.0 : detail.getSubTotal() / detail.getQuantity();

                    log.info("🔍 Item - bookId: {}, quantity: {}, pricePerUnit: {}",
                            detail.getBookId(), detail.getQuantity(), pricePerUnit);

                    return Map.of(
                            "bookId", detail.getBookId(),
                            "quantity", detail.getQuantity(),
                            "price", pricePerUnit);
                }).toList();

                log.info("📦 Criando payload com {} items", items.size());

                var payload = objectMapper.writeValueAsString(new OrderCreatedEvent(
                        order.getId(),
                        order.getUserId(),
                        order.getShippingId(),
                        items,
                        order.getTotalPrice(),
                        order.getOrderDate(),
                        order.getSagaStatus().name()));

                log.info("📤 Payload serializado com sucesso: {} caracteres", payload.length());

                OutboxEvent event = OutboxEvent.builder()
                        .aggregateId(order.getId().toString())
                        .aggregateType("Order")
                        .type("OrderCreated")
                        .payload(payload)
                        .published(false)
                        .createdAt(LocalDateTime.now())
                        .build();

                outboxRepo.save(event);
                log.info("✅ Evento salvo no outbox para orderId {}", orderId);

            } catch (Exception e) {
                log.error("❌ Erro ao criar evento para ordem COMPLETED", e);
                throw new RuntimeException("Erro ao criar evento", e);
            }
        }

        if (status == SagaStatus.FAILED) {
            sendRollbackCommand(orderId);
        }
    }

    public void sendRollbackCommand(Long orderId) {
        try {
            List<OrderDetails> details = detailsRepo.findByOrderId(orderId);

            var items = details.stream().map(detail -> Map.of(
                    "bookId", detail.getBookId(),
                    "quantity", detail.getQuantity())).toList();

            Map<String, Object> payload = Map.of(
                    "orderId", orderId,
                    "items", items);

            String json = objectMapper.writeValueAsString(payload);
            sendKafka("book.rollback.command", orderId.toString(), json);

            log.info("📤 Comando de rollback enviado para orderId {}", orderId);

        } catch (Exception e) {
            throw new RuntimeException("❌ Erro ao enviar comando de rollback", e);
        }
    }

    public void sendBookReserveCommand(Long orderId) {
        Orders order = ordersRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Encomenda não encontrada"));

        List<OrderDetails> details = detailsRepo.findByOrderId(orderId);

        Map<String, Object> payload = buildBookReservePayload(order, details);

        try {
            String payloadJson = objectMapper.writeValueAsString(payload);
            log.info("📤 Enviando comando: book.reserve.command → {}", payloadJson);
            sendKafka("book.reserve.command", order.getId().toString(), payloadJson);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar comando de reserva de livros", e);
        }
    }

    private Map<String, Object> buildBookReservePayload(Orders order, List<OrderDetails> details) {
        var items = details.stream().map(detail -> Map.of(
                "bookId", detail.getBookId(),
                "quantity", detail.getQuantity(),
                "price", detail.getSubTotal() / detail.getQuantity())).toList();

        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", order.getId());
        payload.put("items", items);

        if (order.getShippingId() != null) {
            Map<String, Object> shippingMap = new HashMap<>();
            shippingMap.put("shippingId", order.getShippingId());
            payload.put("shipping", shippingMap);
        }

        return payload;
    }

    public void sendKafka(String topic, String key, String payload) {
        kafkaTemplate.send(topic, key, payload);
    }

    record OrderCreatedEvent(
            Long orderId, Long userId, Long shippingId,
            Object items, double totalPrice, Date orderDate, String sagaStatus) {
    }

    // Consumer for order creation commands

    @KafkaListener(topics = "order.create.command", groupId = "order-service")
    public void onOrderCreateCommand(String message) {
        try {
            CreateOrderRequest request = objectMapper.readValue(message, CreateOrderRequest.class);
            Orders order = createOrder(request);

            var eventPayload = objectMapper.writeValueAsString(Map.of(
                    "orderId", order.getId(),
                    "userId", order.getUserId(),
                    "items", request.getItems(),
                    "shipping", request.getShipping() // pode ser null
            ));

            kafkaTemplate.send("order.created.event", order.getId().toString(), eventPayload);
            log.info("📤 Evento enviado: order.created.event → {}", eventPayload);
        } catch (Exception e) {
            log.error("❌ Erro ao processar order.create.command", e);
        }
    }
}
