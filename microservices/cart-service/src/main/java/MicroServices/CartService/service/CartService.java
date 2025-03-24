package MicroServices.CartService.service;

import java.util.List;

import org.springframework.stereotype.Service;

import MicroServices.CartService.entity.Cart;

@Service
public interface CartService {

    Cart createCart(Cart cart);
    List<Cart> getAllCart();
    Cart getCartIdByUserId(Long userId);
}
