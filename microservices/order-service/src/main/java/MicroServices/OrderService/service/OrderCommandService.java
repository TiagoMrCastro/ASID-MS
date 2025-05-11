// service/OrderCommandService.java
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderCommandService {

    private final OrdersRepository ordersRepo;
    private final OrderDetailsRepository detailsRepo;
    private final OutboxEventRepository outboxRepo;
    private final ObjectMapper objectMapper;

    @Transactional
    public Orders createOrder(CreateOrderRequest request) {
        Orders order = new Orders();
        order.setOrderDate(new Date());
        order.setUserId(request.getUserId());
        order.setShippingId(request.getShippingId());
        order.setSagaStatus(SagaStatus.PENDING); // Pendente

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

        // Criar evento de outbox
        try {
            var payload = objectMapper.writeValueAsString(new OrderCreatedEvent(
                savedOrder.getId(),
                request.getUserId(),
                request.getShippingId(),
                request.getItems(),
                savedOrder.getTotalPrice(),
                savedOrder.getOrderDate(),
                savedOrder.getSagaStatus().name() 
            ));

            OutboxEvent event = OutboxEvent.builder()
                    .aggregateId(savedOrder.getId().toString())
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

        return savedOrder;
    }

    @Transactional
    public void updateSagaStatus(Long orderId, SagaStatus status) {
        Orders order = ordersRepo.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setSagaStatus(status);
        ordersRepo.save(order);
    }


    record OrderCreatedEvent(Long orderId, Long userId, Long shippingId,
                             Object items, double totalPrice, Date orderDate,String sagaStatus) {}
}
