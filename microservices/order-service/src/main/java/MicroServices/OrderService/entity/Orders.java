package MicroServices.OrderService.entity;

import java.util.Date;

import MicroServices.OrderService.enums.SagaStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderid")
    private Long id;

    @Column
    private Date orderDate;

    @Column
    private double totalPrice;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "shippingorder_id")
    private Long shippingId;

    @Enumerated(EnumType.STRING)
    @Column(name = "saga_status")
    private SagaStatus sagaStatus;
}
