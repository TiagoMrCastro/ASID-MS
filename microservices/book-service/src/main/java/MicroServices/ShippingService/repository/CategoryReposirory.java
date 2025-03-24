package MicroServices.ShippingService.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import MicroServices.ShippingService.entity.Category;



@Repository
public interface CategoryReposirory extends JpaRepository<Category,Long>{
    
}
