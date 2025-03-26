// Pacote onde a classe está localizada
package MicroServices.CartService.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import MicroServices.CartService.entity.CartItem;
import MicroServices.CartService.service.CartItemService;


// Controlador REST para manipular as requisições relacionadas aos itens do carrinho
@RestController
public class CartItemController {
    // Injeção de dependência do serviço de itens do carrinho
    @Autowired
    private CartItemService cartItemService;


    /**
     * Endpoint para criar um novo item no carrinho.
     *
     * @param cartItem Objeto CartItem contendo os dados do novo item.
     * @return ResponseEntity contendo o item criado e o status HTTP CREATED.
     */
    @PostMapping("/cartitem")
    public ResponseEntity<CartItem> createCartItem(@RequestBody CartItem cartItem){

        CartItem createdCartItem = cartItemService.createCartItem(cartItem);

        return new ResponseEntity<>(createdCartItem,HttpStatus.CREATED);
 
         
    }
    /**
     * Endpoint para obter todos os itens do carrinho.
     *
     * @return ResponseEntity contendo a lista de todos os itens do carrinho e o status HTTP OK.
     */

    @GetMapping("/cartitem")
    public ResponseEntity<List<CartItem>> getAllCartItem(){

        List<CartItem> cartItems = cartItemService.getAllCartitem();

        return new ResponseEntity<>(cartItems,HttpStatus.OK);
    }
    /**
     * Endpoint para obter um item do carrinho pelo seu ID.
     *
     * @param id O ID do item do carrinho a ser recuperado.
     * @return ResponseEntity contendo o item do carrinho e o status HTTP OK se encontrado, caso contrário, status HTTP NOT FOUND.
     */
    @GetMapping("/cartitem/{id}")
    public ResponseEntity<CartItem> getCartItemById(@PathVariable Long id){

        CartItem existCartItem  = cartItemService.getCartItemById(id);

        if(existCartItem != null) {
            return new ResponseEntity<>(existCartItem , HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    /**
     * Endpoint para obter itens do carrinho pelo nome de usuário.
     *
     * @param username O nome de usuário para recuperar os itens do carrinho.
     * @return ResponseEntity contendo a lista de itens do carrinho e o status HTTP OK.
     */
    @GetMapping("/cartitem/user/{username}")
    public ResponseEntity<List<CartItem>> getCartItemByUsername(@PathVariable String username) {
        List<CartItem> cartItems = cartItemService.getCartItemsByUsername(username);
        return new ResponseEntity<>(cartItems, HttpStatus.OK);
    }
    /**
     * Endpoint para atualizar a quantidade de um item do carrinho.
     *
     * @param id O ID do item do carrinho a ser atualizado.
     * @param cartItem O objeto CartItem contendo a nova quantidade.
     * @return ResponseEntity contendo o item do carrinho atualizado e o status HTTP CREATED.
     */

    @PatchMapping("/quantity/{id}")
    public ResponseEntity<CartItem> patchCartQuantity(@PathVariable Long id , @RequestBody CartItem cartItem){

        CartItem patchedCartItem = cartItemService.patchCartQuantity(id,cartItem);

        return new ResponseEntity<>(patchedCartItem,HttpStatus.CREATED);
    }
    /**
     * Endpoint para atualizar o subtotal de um item do carrinho.
     *
     * @param id O ID do item do carrinho a ser atualizado.
     * @param cartItem O objeto CartItem contendo o novo subtotal.
     * @return ResponseEntity contendo o item do carrinho atualizado e o status HTTP CREATED.
     */

    @PatchMapping("/subtotal/{id}")
    public ResponseEntity<CartItem> patchCartSubTotal(@PathVariable Long id , @RequestBody CartItem cartItem){

        CartItem patchedCartItem = cartItemService.patchCartSubTotal(id,cartItem);

        return new ResponseEntity<>(patchedCartItem,HttpStatus.CREATED);
    }
    /**
     * Endpoint para limpar o carrinho.
     *
     * @return ResponseEntity contendo uma mensagem de confirmação e o status HTTP OK.
     */
    @DeleteMapping("/clearcart")
    public ResponseEntity<String> clearCart(){

        cartItemService.clearCart();
        return ResponseEntity.ok("Cart cleared and Id reset.");
    }
    /**
     * Endpoint para reiniciar o auto incremento do ID do carrinho.
     */

    @PostMapping("/reset")
    public void resetAutoIncrement() {
        cartItemService.resetAutoIncrement();
    }
    /**
     * Endpoint para deletar um item do carrinho pelo seu ID.
     *
     * @param id O ID do item do carrinho a ser deletado.
     * @return ResponseEntity com o status HTTP NO CONTENT.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<CartItem> deleteCartItyItemById(@PathVariable Long id){

        cartItemService.deleteCartItyItemById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    } 
}
