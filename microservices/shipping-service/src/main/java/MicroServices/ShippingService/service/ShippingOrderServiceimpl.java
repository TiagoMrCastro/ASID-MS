package MicroServices.ShippingService.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import MicroServices.ShippingService.entity.ShippingOrder;
import MicroServices.ShippingService.repository.ShippingOrderRepository;

@Service
public class ShippingOrderServiceimpl implements ShippingOrderService{

    @Autowired
    private ShippingOrderRepository shippingOrderRepository;


    @Override
    public ShippingOrder createShippingOrder(ShippingOrder shippingOrder){

        return shippingOrderRepository.save(shippingOrder);

    }

    public List<ShippingOrder> getAllShippingOrders(){


        return shippingOrderRepository.findAll();
    }
    
    @Override
    public Optional<ShippingOrder> getById(Long id) {
        return shippingOrderRepository.findById(id);
    }

}