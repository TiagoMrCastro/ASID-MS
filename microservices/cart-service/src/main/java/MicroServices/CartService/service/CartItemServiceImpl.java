package MicroServices.CartService.service;

import MicroServices.CartService.dto.BookDTO;
import MicroServices.CartService.entity.CartItem;
import MicroServices.CartService.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CartItemServiceImpl implements CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final String BOOK_SERVICE_BASE_URL = "http://localhost:8080/books/"; // via gateway

    @Override
    public CartItem createCartItem(CartItem cartItem) {
        BookDTO book = restTemplate.getForObject(BOOK_SERVICE_BASE_URL + cartItem.getBookId(), BookDTO.class);

        if (book == null || book.getQuantity() < cartItem.getQuantity()) {
            throw new RuntimeException("Book not available or insufficient stock");
        }

        cartItem.setUnitPrice(book.getPrice());
        cartItem.setSubTotal(book.getPrice() * cartItem.getQuantity());

        return cartItemRepository.save(cartItem);
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