package com.asid.order_saga_orchestrator.client;

import com.asid.order_saga_orchestrator.dto.CreateOrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class OrderServiceClient {

    private final WebClient.Builder webClientBuilder;

    public Mono<Map<String, Object>> createOrder(CreateOrderRequest request) {
        return webClientBuilder.build()
                .post()
                .uri("http://order-service:8080/orders")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }

    public Mono<Void> cancelOrder(Long orderId) {
        return webClientBuilder.build()
                .put()
                .uri("http://order-service:8080/orders/" + orderId + "/cancel")
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<Void> completeOrder(Long orderId) {
        return webClientBuilder.build()
                .put()
                .uri("http://order-service:8080/orders/" + orderId + "/complete")
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<Void> updateShippingId(Long orderId, Long shippingId) {
        return webClientBuilder.build()
                .put()
                .uri("http://order-service:8080/orders/{orderId}/shipping/{shippingId}", orderId, shippingId)
                .retrieve()
                .bodyToMono(Void.class);
    }

}
