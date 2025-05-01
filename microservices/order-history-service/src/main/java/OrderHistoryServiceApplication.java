package main.java.orderhistoryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableKafka
public class OrderHistoryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderHistoryServiceApplication.class, args);
    }
}
