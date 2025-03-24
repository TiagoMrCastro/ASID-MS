package MicroServices.BookService.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import MicroServices.BookService.entity.Category;



@Repository
public interface CategoryReposirory extends JpaRepository<Category,Long>{
    
}
