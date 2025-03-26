
package MicroServices.CartService.controller;// Controlador REST para manipular as requisições relacionadas ao carrinho de compras

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import MicroServices.CartService.entity.Cart;
// import MicroServices.CartService.repository.UserRepository;
import MicroServices.CartService.service.CartService;

@RestController
public class CartController {// Injeção de dependência do serviço de carrinho de compras
    @Autowired
    private CartService cartService;

    /**
     * Endpoint para criar um novo carrinho de compras.
     *
     * @param createCart Objeto Cart contendo os dados do novo carrinho.
     * @return ResponseEntity contendo o carrinho criado e o status HTTP CREATED.
     */
    @PostMapping("/cart")
    public ResponseEntity<Cart> createCart(@RequestBody Cart createCart) {
        Cart updatedCart = cartService.createCart(createCart);
        return new ResponseEntity<>(updatedCart, HttpStatus.CREATED);
    }

    /**
     * Endpoint para obter todos os carrinhos de compras.
     *
     * @return ResponseEntity contendo a lista de todos os carrinhos e o status HTTP OK.
     */
    @GetMapping("/cart")
    public ResponseEntity<List<Cart>> getAllCart() {
        List<Cart> existcart = cartService.getAllCart();
        return new ResponseEntity<>(existcart, HttpStatus.OK);
    }

    /**
     * Endpoint para obter o carrinho de compras pelo ID do usuário.
     *
     * @param userId O ID do usuário para recuperar o carrinho.
     * @return ResponseEntity contendo o carrinho e o status HTTP OK se encontrado, caso contrário, status HTTP NOT FOUND.
     */
    @GetMapping("/cart/{userId}")
    public ResponseEntity<Cart> getCartIdByUserId(@PathVariable Long userId) {
        Cart cartId = cartService.getCartIdByUserId(userId);
        if (cartId != null) {
            return new ResponseEntity<>(cartId, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
