package MicroServices.ShippingService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import MicroServices.ShippingService.entity.ShippingOrder;

@Repository
public interface ShippingOrderRepository extends JpaRepository<ShippingOrder,Long>{

}
