package com.cho.ecommerce.domain.product.service;

import com.cho.ecommerce.domain.product.entity.CategoryEntity;
import com.cho.ecommerce.domain.product.repository.CategoryRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository; // Assuming you have a JPA repository
    
    public CategoryEntity createCategory(CategoryEntity category) {
        return categoryRepository.save(category);
    }
    
    public CategoryEntity getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));
    }
    
    public CategoryEntity updateCategory(Long categoryId, CategoryEntity updatedCategory) {
        CategoryEntity existingCategory = getCategoryById(categoryId);
        existingCategory.update(updatedCategory.getCategoryCode(), updatedCategory.getName());
        
        return categoryRepository.save(existingCategory);
    }
    
    public void deleteCategory(Long categoryId) {
        CategoryEntity category = getCategoryById(categoryId);
        categoryRepository.delete(category);
    }
    
    public List<CategoryEntity> getAllCategories() {
        return categoryRepository.findAll();
    }
    
}

