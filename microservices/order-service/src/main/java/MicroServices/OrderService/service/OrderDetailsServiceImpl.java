package MicroServices.OrderService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import MicroServices.OrderService.entity.OrderDetails;
import MicroServices.OrderService.repository.OrderDetailsRepository;

@Service
public class OrderDetailsServiceImpl implements OrderDetailsService{
    
    @Autowired
    private OrderDetailsRepository orderDetailsRepository;


    public OrderDetails createOrderDetails(OrderDetails orderDetails){

        return orderDetailsRepository.save(orderDetails);

    }
}
