package main.java.orderhistoryservice.repository;


import main.java.orderhistoryservice.entity.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
    List<OrderHistory> findByUserIdAndOrderDateBetween(Long userId, LocalDate from, LocalDate to);
}
