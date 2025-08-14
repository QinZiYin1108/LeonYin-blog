package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService extends IService<Category> {

    List<Category> getEnabledCategories();

    List<Category> getCategoriesBySort();

    Category createCategory(Category category);

    boolean updateCategory(String categoryId, Category category);

    boolean deleteCategory(String categoryId);

    boolean toggleCategoryStatus(String categoryId);
}






