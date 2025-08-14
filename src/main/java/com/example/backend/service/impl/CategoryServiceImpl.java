package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.entity.Category;
import com.example.backend.entity.SystemConfig;
import com.example.backend.mapper.CategoryMapper;
import com.example.backend.service.CategoryService;
import com.example.backend.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类服务实现类
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    
    @Autowired
    private CategoryMapper categoryMapper;
        
        @Autowired
        private SystemConfigService systemConfigService;
    
    @Override
    public List<Category> getEnabledCategories() {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1)
                   .orderByAsc("sort_order");
        
        return categoryMapper.selectList(queryWrapper);
    }
    
    @Override
    public List<Category> getCategoriesBySort() {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort_order")
                   .orderByAsc("create_time");
        
        return categoryMapper.selectList(queryWrapper);
    }
    
    @Override
    public Category createCategory(Category category) {
        // 如果未提供分类图标，尝试使用系统默认分类图标
        if (category.getIconImageId() == null || category.getIconImageId().trim().isEmpty()) {
            SystemConfig cfg = systemConfigService.getByKey("default_category_icon_image_id");
            if (cfg != null && cfg.getConfigValue() != null && !cfg.getConfigValue().trim().isEmpty()) {
                category.setIconImageId(cfg.getConfigValue());
            }
        }
        categoryMapper.insert(category);
        return category;
    }
    
    @Override
    public boolean updateCategory(String categoryId, Category category) {
        Category existCategory = categoryMapper.selectById(categoryId);
        if (existCategory == null) {
            return false;
        }
        
        category.setId(categoryId);
        category.updateTime();
        return categoryMapper.updateById(category) > 0;
    }
    
    @Override
    public boolean deleteCategory(String categoryId) {
        // 注意：实际项目中需要检查该分类下是否还有文章
        // 如果有文章，应该禁止删除或者转移文章到其他分类
        return categoryMapper.deleteById(categoryId) > 0;
    }
    
    @Override
    public boolean toggleCategoryStatus(String categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            return false;
        }
        
        category.setStatus(category.getStatus() == 1 ? 0 : 1);
        category.updateTime();
        return categoryMapper.updateById(category) > 0;
    }
} 
 