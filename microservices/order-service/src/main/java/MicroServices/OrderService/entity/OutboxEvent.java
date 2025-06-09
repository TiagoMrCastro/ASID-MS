package MicroServices.OrderService.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "outbox_event")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateId;
    private String aggregateType;
    private String type;

    @Column(columnDefinition = "TEXT")
    @Lob
    private String payload;

    private Boolean published;

    private java.time.LocalDateTime createdAt;
}