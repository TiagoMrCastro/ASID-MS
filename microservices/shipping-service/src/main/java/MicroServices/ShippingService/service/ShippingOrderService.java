package MicroServices.ShippingService.service;

import java.util.List;

import org.springframework.stereotype.Service;

import MicroServices.ShippingService.entity.ShippingOrder;

import java.util.Optional;


@Service
public interface ShippingOrderService {

    ShippingOrder createShippingOrder(ShippingOrder shippingOrder);

    List<ShippingOrder> getAllShippingOrders();

    Optional<ShippingOrder> getById(Long id);
}
