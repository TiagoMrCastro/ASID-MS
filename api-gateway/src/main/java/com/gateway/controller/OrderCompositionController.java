package com.gateway.controller;

import com.gateway.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpStatus;


import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderCompositionController {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @GetMapping("/details/{orderId}")
    public Mono<ResponseEntity<OrderFullDetailsDto>> getOrderDetails(@PathVariable Long orderId) {
    
        Mono<OrderDto> orderMono = webClientBuilder.build()
                .get()
                .uri("http://order-service:8080/orders/" + orderId)
                .retrieve()
                .bodyToMono(OrderDto.class);
    
        return orderMono.flatMap(order -> {
    
            Long userId = order.getUserId();
            Long shippingId = order.getShippingId();
            List<Long> bookIds = order.getBookIds();
    
            Mono<UserDto> userMono = webClientBuilder.build()
                    .get()
                    .uri("http://user-service:8080/users/" + userId)
                    .retrieve()
                    .bodyToMono(UserDto.class);
    
            Mono<ShippingDto> shippingMono = webClientBuilder.build()
                    .get()
                    .uri("http://shipping-service:8080/shipping/" + shippingId)
                    .retrieve()
                    .bodyToMono(ShippingDto.class);
    
                    List<Mono<BookDto>> bookMonos = bookIds.stream()
                    .map(bookId -> webClientBuilder.build()
                        .get()
                        .uri("http://book-service:8080/books/" + bookId)
                        .retrieve()
                        .onStatus(status -> status.is4xxClientError(), response -> {
                            System.out.println("Livro não encontrado: " + bookId);
                            return Mono.error(new RuntimeException("Livro não encontrado: " + bookId));
                        })
                        .onStatus(status -> status.is5xxServerError(), response -> {
                            System.out.println("Erro interno no book-service para o livro: " + bookId);
                            return Mono.error(new RuntimeException("Erro no book-service"));
                        })
                        .bodyToMono(BookDto.class)
                    )
                    .collect(java.util.stream.Collectors.toList());                
                                
    
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
