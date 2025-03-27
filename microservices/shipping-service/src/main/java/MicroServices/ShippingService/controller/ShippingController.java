package MicroServices.ShippingService.controller;

import java.util.List;

import MicroServices.ShippingService.entity.ShippingOrder;
import MicroServices.ShippingService.service.ShippingOrderService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



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
    }
