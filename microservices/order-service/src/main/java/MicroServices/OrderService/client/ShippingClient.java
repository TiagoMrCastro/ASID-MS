package MicroServices.OrderService.client;

import MicroServices.OrderService.dto.ShippingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class ShippingClient {

    private final WebClient.Builder webClientBuilder;

    public Long createShipping(ShippingDto dto) {
        return webClientBuilder.build()
            .post()
            .uri("http://shipping-service:8080/shipping")
            .bodyValue(dto)
            .retrieve()
            .bodyToMono(Long.class)
            .block(); // Bloqueia até receber a resposta
    }
}
