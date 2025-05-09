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

    @Override
    public Category addCategory(Category category) {
        return categoryReposirory.save(category);
    }

    @Override
    public Category updateCategory(Long id, Category category) {
        return categoryReposirory.findById(id).map(existingCategory -> {
            existingCategory.setName(category.getName());
            return categoryReposirory.save(existingCategory);
        }).orElse(null);
    }

}
