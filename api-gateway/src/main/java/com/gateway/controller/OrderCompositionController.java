package com.gateway.controller;

import com.gateway.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderCompositionController {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @GetMapping("/details/{orderId}")
    public Mono<ResponseEntity<OrderFullDetailsDto>> getOrderDetails(@PathVariable Long orderId) {

        // 1. Buscar dados da encomenda
        Mono<OrderDto> orderMono = webClientBuilder.build()
                .get()
                .uri("http://order-service/orders/" + orderId)
                .retrieve()
                .bodyToMono(OrderDto.class);

        return orderMono.flatMap(order -> {

            // Exemplo estático — ajustar se tiveres estes dados vindos do order-service
            Long userId = 1L;
            Long shippingId = 1L;
            List<Long> bookIds = List.of(1L, 2L); // <- substituir com dados reais do order

            Mono<UserDto> userMono = webClientBuilder.build()
                    .get()
                    .uri("http://user-service/users/" + userId)
                    .retrieve()
                    .bodyToMono(UserDto.class);

            Mono<ShippingDto> shippingMono = webClientBuilder.build()
                    .get()
                    .uri("http://shipping-service/shipping/" + shippingId)
                    .retrieve()
                    .bodyToMono(ShippingDto.class);

            List<Mono<BookDto>> bookMonos = bookIds.stream()
                    .map(bookId -> webClientBuilder.build()
                            .get()
                            .uri("http://book-service/books/" + bookId)
                            .retrieve()
                            .bodyToMono(BookDto.class))
                    .toList();

            Mono<List<BookDto>> booksMono = Flux.merge(bookMonos).collectList();

            return Mono.zip(userMono, shippingMono, booksMono)
                    .map(tuple -> {
                        UserDto user = tuple.getT1();
                        ShippingDto shipping = tuple.getT2();
                        List<BookDto> books = tuple.getT3();

                        OrderFullDetailsDto details = new OrderFullDetailsDto();
                        details.setOrder(order);
                        details.setUser(user);
                        details.setShipping(shipping);
                        details.setBooks(books);

                        return ResponseEntity.ok(details);
                    });
        });
    }
}
