package com.gateway.controller;

import com.gateway.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartCompositionController {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @PostMapping("/add")
    public Mono<CartItemResponse> addToCart(@RequestBody AddToCartRequest request) {
        WebClient webClient = webClientBuilder.build();

        // Buscar dados do usuário
        Mono<UserDto> userMono = webClient.get()
            .uri("lb://user-service/users/" + request.getUserId())
            .retrieve()
            .bodyToMono(UserDto.class);

        // Buscar dados do livro
        Mono<BookDto> bookMono = webClient.get()
            .uri("lb://book-service/books/" + request.getBookId())
            .retrieve()
            .bodyToMono(BookDto.class);

        return Mono.zip(userMono, bookMono).flatMap(tuple -> {
            UserDto user = tuple.getT1();
            BookDto book = tuple.getT2();

            CartItemDto cartItem = new CartItemDto();
            cartItem.setUserId(request.getUserId());
            cartItem.setBookId(request.getBookId());
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(book.getPrice());

            // Enviar item ao cart-service
            return webClient.post()
                .uri("lb://cart-service/cart/items")
                .bodyValue(cartItem)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> {
                    System.out.println("Erro 4xx no cart-service");
                    return Mono.error(new RuntimeException("Erro no cart-service (4xx)"));
                })
                .onStatus(status -> status.is5xxServerError(), response -> {
                    System.out.println("Erro 5xx no cart-service");
                    return Mono.error(new RuntimeException("Erro interno no cart-service"));
                })
                .bodyToMono(CartItemResponse.class)
                .map(response -> {
                    response.setBookTitle(book.getTitle());
                    response.setPrice(book.getPrice());
                    response.setQuantity(request.getQuantity());
                    response.setUserName(user.getUsername());

                    double subtotal = book.getPrice() * request.getQuantity();
                    response.setSubtotal(subtotal);

                    return response;
                });
        });
    }
}
