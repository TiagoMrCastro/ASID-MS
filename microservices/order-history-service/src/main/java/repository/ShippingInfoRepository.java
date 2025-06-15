
package main.java.orderhistoryservice.repository;


import main.java.orderhistoryservice.entity.ShippingInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingInfoRepository extends JpaRepository<ShippingInfo, Long> {}
