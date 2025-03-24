package MicroServices.BookService.service;



import MicroServices.BookService.entity.Category;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface CategoryService {
    List<Category> getAllCategories();
}
