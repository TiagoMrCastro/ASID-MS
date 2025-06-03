package MicroServices.OrderService.service;

import MicroServices.OrderService.client.ShippingClient;
import MicroServices.OrderService.dto.CreateOrderRequest;
import MicroServices.OrderService.dto.ShippingDto;
import MicroServices.OrderService.entity.OrderDetails;
import MicroServices.OrderService.entity.Orders;
import MicroServices.OrderService.entity.OutboxEvent;
import MicroServices.OrderService.enums.SagaStatus;
import MicroServices.OrderService.repository.OrderDetailsRepository;
import MicroServices.OrderService.repository.OrdersRepository;
import MicroServices.OrderService.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderCommandService {

    private final OrdersRepository ordersRepo;
    private final OrderDetailsRepository detailsRepo;
    private final OutboxEventRepository outboxRepo;
    private final ObjectMapper objectMapper;
    private final ShippingClient shippingClient;

    @Transactional
    public Orders createOrder(CreateOrderRequest request) {
        Orders order = new Orders();
        order.setOrderDate(new Date());
        order.setUserId(request.getUserId());
        order.setShippingId(null); // shipping será adicionado depois
        order.setSagaStatus(SagaStatus.PENDING);

        double total = request.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        order.setTotalPrice(total);

        Orders savedOrder = ordersRepo.save(order);

        // Criar detalhes
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

                var items = details.stream().map(detail -> Map.of(
                        "bookId", detail.getBookId(),
                        "quantity", detail.getQuantity(),
                        "price", detail.getSubTotal() / detail.getQuantity()
                )).toList();

                var payload = objectMapper.writeValueAsString(new OrderCreatedEvent(
                        order.getId(),
                        order.getUserId(),
                        order.getShippingId(),
                        items,
                        order.getTotalPrice(),
                        order.getOrderDate(),
                        order.getSagaStatus().name()
                ));

                OutboxEvent event = OutboxEvent.builder()
                        .aggregateId(order.getId().toString())
                        .aggregateType("Order")
                        .type("OrderCreated")
                        .payload(payload)
                        .published(false)
                        .createdAt(LocalDateTime.now())
                        .build();

                outboxRepo.save(event);
            } catch (Exception e) {
                throw new RuntimeException("Erro ao criar evento", e);
            }
        }
    }

    @Transactional
    public void attachShipping(Long orderId, ShippingDto shipping) {
        Orders order = ordersRepo.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Encomenda não encontrada"));

        // Chamar o shipping-service e criar envio
        Long shippingId = shippingClient.createShipping(shipping); // Chamada HTTP ao outro microserviço

        // Associar à order existente
        order.setShippingId(shippingId);

        // Save da order com shippingId
        ordersRepo.save(order);
    }


    public void sendBookReserveCommand(Long orderId) {
        Orders order = ordersRepo.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Encomenda não encontrada"));

        List<OrderDetails> details = detailsRepo.findByOrderId(orderId);

        var items = details.stream().map(detail -> Map.of(
                "bookId", detail.getBookId(),
                "quantity", detail.getQuantity(),
                "price", detail.getSubTotal() / detail.getQuantity()
        )).toList();

        Map<String, Object> payload = Map.of(
                "orderId", order.getId(),
                "items", items,
                "shipping", Map.of(
                        "shippingId", order.getShippingId()
                )
        );

        try {
            String payloadJson = objectMapper.writeValueAsString(payload);
            sendKafka("book.reserve.command", order.getId().toString(), payloadJson);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar comando de reserva de livros", e);
        }
    }


    record OrderCreatedEvent(Long orderId, Long userId, Long shippingId,
                             Object items, double totalPrice, Date orderDate, String sagaStatus) {
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendKafka(String topic, String key, String payload) {
        kafkaTemplate.send(topic, key, payload);
    }

}
