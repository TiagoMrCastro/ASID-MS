package MicroServices.ShippingService.service;



import MicroServices.ShippingService.entity.Category;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface CategoryService {
    List<Category> getAllCategories();
}
