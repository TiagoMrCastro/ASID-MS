package com.asid.order_saga_orchestrator.client;

import com.asid.order_saga_orchestrator.dto.CartItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookServiceClient {

    private final WebClient.Builder webClientBuilder;

    public Mono<Boolean> reserveBooks(List<CartItemDto> items) {
        return Flux.fromIterable(items)
            .flatMap(item ->
                webClientBuilder.build()
                    .patch()
                    .uri("http://book-service:8080/books/{id}/adjustQuantity?delta={delta}",
                            item.getBookId(), -item.getQuantity())
                    .retrieve()
                    .bodyToMono(Void.class)  
            )
            .then(Mono.just(true)) // se todos passaram sem erro
            .onErrorResume(e -> {
                System.err.println("❌ Erro ao reservar livros: " + e.getMessage());
                return Mono.just(false); // se algum falhou
            });
    }


    public Mono<Void> rollbackReservation(List<CartItemDto> items) {
        return Flux.fromIterable(items)
            .flatMap(item ->
                webClientBuilder.build()
                    .patch()
                    .uri("http://book-service:8080/books/{id}/adjustQuantity?delta={delta}",
                         item.getBookId(), item.getQuantity())
                    .retrieve()
                    .toBodilessEntity()
            )
            .then(); // retorna Mono<Void>
    }
}
