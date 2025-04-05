package MicroServices.CartService.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import MicroServices.CartService.entity.Cart;


@Repository
public interface CartRepository extends JpaRepository <Cart , Long>{

    Cart getCartIdByUserId(Long userId);

}