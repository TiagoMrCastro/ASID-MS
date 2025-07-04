package MicroServices.OrderService.repository;

import MicroServices.OrderService.entity.Orders;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    Optional<Orders> findTopByUserIdOrderByOrderDateDesc(Long userId);

}

