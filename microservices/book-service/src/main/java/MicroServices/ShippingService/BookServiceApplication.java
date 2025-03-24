<<<<<<<< HEAD:microservices/order-service/src/main/java/MicroServices/OrderService/OrderServiceApplication.java
package MicroServices.OrderService;
========
package MicroServices.ShippingService;
>>>>>>>> 5d7ee94675a15c469f27aa101b1c1342ccc5eb08:microservices/book-service/src/main/java/MicroServices/ShippingService/BookServiceApplication.java

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

}
