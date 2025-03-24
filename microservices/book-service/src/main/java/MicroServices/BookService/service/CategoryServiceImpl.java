package MicroServices.BookService.service;


import MicroServices.BookService.entity.Category;
import MicroServices.BookService.repository.CategoryReposirory;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CategoryServiceImpl implements CategoryService{


    private final CategoryReposirory categoryReposirory;

    public CategoryServiceImpl(CategoryReposirory categoryReposirory) {
        this.categoryReposirory = categoryReposirory;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryReposirory.findAll();
    }
}
