package MicroServices.CartService.service;

import MicroServices.CartService.dto.BookDTO;
import MicroServices.CartService.entity.Cart;
import MicroServices.CartService.entity.CartItem;
import MicroServices.CartService.repository.CartItemRepository;
import MicroServices.CartService.repository.CartRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@Service
public class CartItemServiceImpl implements CartItemService {

    @Autowired
    private CartRepository cartRepository;


    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private RestTemplate restTemplate;


   @Override
    public CartItem createCartItem(CartItem cartItem) {
    // 1. Obter ou criar carrinho do utilizador
        Cart cart = cartRepository.getCartIdByUserId(cartItem.getUserId());

        if (cart == null) {
            cart = new Cart();
            cart.setUserId(cartItem.getUserId());
            cart.setCreatedDate(LocalDate.now());
            cart = cartRepository.save(cart);
        }

        // 2. Obter info do livro
        try {
            BookDTO book = restTemplate.getForObject("http://book-service:8080/books/" + cartItem.getBookId(), BookDTO.class);
            
            if (book == null) {
                throw new RuntimeException("Book not found.");
            }
        
            if (book.getQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for book ID: " + book.getId());
            }
        
            cartItem.setCart(cart);
            cartItem.setUnitPrice(book.getPrice());
            cartItem.setSubTotal(book.getPrice() * cartItem.getQuantity());
        
            return cartItemRepository.save(cartItem);
        
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao obter livro: " + ex.getMessage(), ex);
        }
        
    }

    @Override
    public List<CartItem> getAllCartitem() {
        return cartItemRepository.findAll();
    }

    @Override
    public CartItem getCartItemById(Long id) {
        return cartItemRepository.findById(id).orElse(null);
    }

    @Override
    public CartItem patchCartQuantity(Long id, CartItem cartItem) {
        CartItem existItem = cartItemRepository.findById(id).orElse(null);
        if (existItem != null) {
            existItem.setQuantity(cartItem.getQuantity());
            return cartItemRepository.save(existItem);
        }
        return null;
    }

    @Override
    public CartItem patchCartSubTotal(Long id, CartItem cartItem) {
        CartItem existItem = cartItemRepository.findById(id).orElse(null);
        if (existItem != null) {
            existItem.setSubTotal(cartItem.getSubTotal());
            return cartItemRepository.save(existItem);
        }
        return null;
    }

    @Override
    public CartItem deleteCartItyItemById(Long id) {
        CartItem existItem = cartItemRepository.findById(id).orElse(null);
        if (existItem != null) {
            cartItemRepository.delete(existItem);
        }
        return null;
    }

    @Override
    public void clearCart() {
        cartItemRepository.deleteAll();
    }

    @Override
    public void resetAutoIncrement() {
        cartItemRepository.resetAutoIncrement();
    }

    @Override
    public List<CartItem> getCartItemsByUsername(String username) {
        throw new UnsupportedOperationException("Fetching by username is deprecated. Use userId instead.");
    }
}