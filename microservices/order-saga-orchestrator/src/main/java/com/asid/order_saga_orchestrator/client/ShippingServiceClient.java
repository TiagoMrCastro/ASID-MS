package com.asid.order_saga_orchestrator.client;

import com.asid.order_saga_orchestrator.dto.ShippingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ShippingServiceClient {

    private final WebClient.Builder webClientBuilder;

    public Mono<ShippingDto> createShipping(ShippingDto dto) {
        return webClientBuilder.build()
                .post()
                .uri("http://shipping-service:8080/shipping")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(ShippingDto.class); 
    }

}
