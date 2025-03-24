package MicroServices.ShippingService.controller;

import MicroServices.ShippingService.entity.Category;
import MicroServices.ShippingService.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class CategoryController {
    @Autowired
    private CategoryService categoryService;


     @GetMapping("/category")
     public ResponseEntity<List<Category>> getAllCategory(){
        List<Category> categories = categoryService.getAllCategories();
         return new ResponseEntity<>(categories, HttpStatus.OK);
     }

   
}
