package MicroServices.ShippingService.controller;

import java.util.List;

import MicroServices.ShippingService.dto.ShippingDto;
import MicroServices.ShippingService.entity.ShippingOrder;
import MicroServices.ShippingService.service.ShippingOrderService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import MicroServices.ShippingService.dto.ShippingDto;

import org.springframework.web.bind.annotation.PathVariable;



@RestController
    public class ShippingController {

        @Autowired
        private ShippingOrderService shippingOrderService;

        @PostMapping("/shipping")
        public ResponseEntity<ShippingOrder> createShippingOrder(@RequestBody ShippingOrder shippingOrder){

            ShippingOrder shippedOrder = shippingOrderService.createShippingOrder(shippingOrder);

            return new ResponseEntity<>(shippedOrder, HttpStatus.CREATED);
        }

        @GetMapping("/shipping")
        public ResponseEntity<List<ShippingOrder>> getAllShippingOrders(){

            List<ShippingOrder> shippedOrder = shippingOrderService.getAllShippingOrders();

            return new ResponseEntity<>(shippedOrder,HttpStatus.OK);

        }
        
        @GetMapping("/shipping/{id}")
        public ResponseEntity<?> getShippingOrderById(@PathVariable Long id) {
        return shippingOrderService.getById(id)
            .map(order -> {
                ShippingDto dto = new ShippingDto();
                dto.setId(order.getId());
                dto.setFirstName(order.getFirstName());
                dto.setLastName(order.getLastName());
                dto.setAddress(order.getAddress());
                dto.setCity(order.getCity());
                dto.setEmail(order.getEmail());
                dto.setPostalCode(order.getPostalCode());
                return ResponseEntity.ok(dto);
            })
            .orElse(ResponseEntity.notFound().build());
        }

        @DeleteMapping("/shipping/{id}")
        public ResponseEntity<Void> cancelShipping(@PathVariable Long id) {
            shippingOrderService.deleteById(id);
            return ResponseEntity.ok().build();
        }

    }
