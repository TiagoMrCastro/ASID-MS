package MicroServices.ShippingService.service;

import java.util.List;

import org.springframework.stereotype.Service;

import MicroServices.ShippingService.entity.ShippingOrder;



@Service
public interface ShippingOrderService {

    ShippingOrder createShippingOrder(ShippingOrder shippingOrder);

    List<ShippingOrder> getAllShippingOrders();
}
