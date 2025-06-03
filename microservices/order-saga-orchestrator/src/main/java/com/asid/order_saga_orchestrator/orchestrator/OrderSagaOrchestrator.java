// package com.asid.order_saga_orchestrator.orchestrator;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.asid.order_saga_orchestrator.client.BookServiceClient;
// import com.asid.order_saga_orchestrator.client.OrderServiceClient;
// import com.asid.order_saga_orchestrator.client.ShippingServiceClient;
// import com.asid.order_saga_orchestrator.dto.CreateOrderRequest;
// import com.asid.order_saga_orchestrator.dto.ShippingDto;
// import com.asid.order_saga_orchestrator.kafka.KafkaEventPublisher;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;
// import reactor.core.publisher.Mono;

// import java.util.Map;

// @Service
// @RequiredArgsConstructor
// public class OrderSagaOrchestrator {

//     private final KafkaEventPublisher kafkaPublisher;
//     private final ObjectMapper objectMapper;
//     private final OrderServiceClient orderClient;
//     private final BookServiceClient bookClient;
//     private final ShippingServiceClient shippingClient;

//     public Mono<String> executeSaga(CreateOrderRequest request) {
//         return orderClient.createOrder(request)
//                 .flatMap(response -> {
//                     Long orderId = Long.parseLong(response.get("id").toString());

//                     return bookClient.reserveBooks(request.getItems())
//                             .flatMap(reserved -> {
//                                 if (!reserved) {
//                                     return orderClient.cancelOrder(orderId)
//                                             .then(sendKafkaFailure(orderId, "RESERVATION_FAILED"))
//                                             .thenReturn("❌ Falha na reserva de stock. Encomenda cancelada.");
//                                 }

//                                 return shippingClient.createShipping(request.getShipping())
//                                         .flatMap(shippedDto -> {
//                                             Long shippingId = shippedDto.getId();

//                                             return orderClient.updateShippingId(orderId, shippingId)
//                                                     .then(orderClient.completeOrder(orderId))
//                                                     .then(sendKafkaSuccess(orderId, shippingId))
//                                                     .thenReturn("✅ Encomenda finalizada com sucesso.");
//                                         })
//                                         .onErrorResume(e -> {
//                                             return bookClient.rollbackReservation(request.getItems())
//                                                     .then(orderClient.cancelOrder(orderId))
//                                                     .then(sendKafkaFailure(orderId, "SHIPPING_FAILED"))
//                                                     .thenReturn("❌ Falha ao criar envio. Encomenda cancelada.");
//                                         });
//                             });
//                 })
//                 .onErrorResume(e -> Mono.just("❌ Erro inesperado: " + e.getMessage()));
//     }

//     private Mono<Void> sendKafkaSuccess(Long orderId, Long shippingId) {
//         try {
//             String payload = objectMapper.writeValueAsString(Map.of(
//                     "orderId", orderId,
//                     "shippingId", shippingId,
//                     "status", "COMPLETED"));
//             kafkaPublisher.publish("order.saga.success", orderId.toString(), payload);
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//         return Mono.empty();
//     }

//     private Mono<Void> sendKafkaFailure(Long orderId, String reason) {
//         try {
//             String payload = objectMapper.writeValueAsString(Map.of(
//                     "orderId", orderId,
//                     "status", "FAILED",
//                     "reason", reason));
//             kafkaPublisher.publish("order.saga.failure", orderId.toString(), payload);
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//         return Mono.empty();
//     }
// }
